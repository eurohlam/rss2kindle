package org.roag.camel;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import org.apache.camel.*;
import org.apache.camel.dataformat.rss.RssConverter;
import org.roag.ds.SubscriberRepository;
import org.roag.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;


/**
 * Created by eurohlam on 03.10.16.
 */
@Component
@SuppressWarnings("unused")
public class Rss2XmlHandler {

    private final Logger logger = LoggerFactory.getLogger(Rss2XmlHandler.class);

    private CamelContext camelContext;
    private SubscriberRepository subscriberRepository;
    private ExecutorService taskExecutor;
    private ExecutorService resultExecutor;

    @Value("${rss.splitEntries}")
    private String splitEntries;

    @Value("${rss.feedHeader}")
    private String feedHeaders;

    @Value("${rss.consumer.delay}")
    private String consumerDelay;

    @Value("${storage.path.rss}")
    private String storagePathRss;

    @Value("${rss.lastUpdate.count}")
    private int lastUpdateCount;

    @Value("${rss.lastUpdate.timeunit}")
    private String lastUpdateTimeunit;

    @Autowired
    public Rss2XmlHandler(@Qualifier("subscriberRepository") SubscriberRepository subscriberRepository,
                          @Qualifier("mainCamelContext") CamelContext camelContext) {
        this.subscriberRepository = subscriberRepository;
        this.camelContext = camelContext;
        this.taskExecutor = Executors.newCachedThreadPool();
        this.resultExecutor = Executors.newCachedThreadPool();
    }

    public void runRssPollingForAllUsers() throws Exception {
        logger.info("Start polling for all active users");
        for (User user : subscriberRepository.getUserRepository().findAll())
            if (UserStatus.fromValue(user.getStatus()) == UserStatus.ACTIVE)
                runRssPollingForList(user.getUsername(), user.getSubscribers());
            else
                logger.info("User {} is locked and will not be processed", user.getUsername());
    }

    /**
     * @param username       - unique name of user
     * @param subscriberList - list of subscribers that belong to current user
     */
    public void runRssPollingForList(String username, List<Subscriber> subscriberList) {
        for (Subscriber subscriber : subscriberList) {
            if (SubscriberStatus.fromValue(subscriber.getStatus()) == SubscriberStatus.ACTIVE) {
                runRssPollingForSubscriber(username, subscriber);
            } else {
                logger.info("Subscriber {} of user {} is suspended and will not be processed", subscriber.getEmail(), username);
            }
        }
    }


    /**
     * @param username - unique name of user
     * @param email    - email of subscriber that belongs to current user
     * @throws Exception
     */
    public void runRssPollingForSubscriber(String username, String email) throws Exception {
        runRssPollingForSubscriber(username, subscriberRepository.getSubscriber(username, email));
    }

    /**
     * @param username   - unique name of user
     * @param subscriber - subscriber that belongs to current user
     */
    public void runRssPollingForSubscriber(String username, Subscriber subscriber) {
        logger.debug("Initiated polling for: username = {}; subscriber = {}; subscriber.name = {}", username, subscriber.getEmail(), subscriber.getName());
        Map<Rss, Future<RssPollingTask.PollingTaskResult>> taskMap = new HashMap<>(subscriber.getRsslist().size());
        //run polling rss asynchronously
        for (int i = 0; i < subscriber.getRsslist().size(); i++) {
            Rss rss = subscriber.getRsslist().get(i);
            if (RssStatus.DEAD != RssStatus.fromValue(rss.getStatus())) {
                logger.info("Got task for username={}; subscriber = {}, rss = {}", username, subscriber.getEmail(), rss.getRss());
                rss.setLastPollingDate(LocalDateTime.now().toString());
                try {
                    Future<RssPollingTask.PollingTaskResult> result = taskExecutor.submit(
                            new RssPollingTask(rss, getPathForRss(username, subscriber.getEmail(), rss.getRss())));
                    taskMap.put(rss, result);

                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        //check polling rss results
        resultExecutor.submit(new PollingResultHandler(username, subscriber, taskMap));
    }


    private String getPathForRss(String username, String email, String rss) {
        String fileUri = storagePathRss +
                username + "/" +
                email + "/"
                + rss.replace('/', '_').replace(":", "_");
        logger.debug("Got URI for file {}", fileUri);
        return fileUri;
    }


    private enum TaskStatus {NOT_STARTED, COMPLETED}

    class RssPollingTask implements Callable<RssPollingTask.PollingTaskResult> {

        private final DateTimeFormatter rssFileNameFormat = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        private final Rss rss;
        private final String rssURI;
        private final String path;
        private final String fileName;

        RssPollingTask(Rss rss, String path) {
            this.rss = rss;
            this.rssURI = getCamelHttp4Uri(rss.getRss());
            this.path = path;
            this.fileName = rssFileNameFormat.format(LocalDateTime.now());
        }

        private String getCamelHttp4Uri(String rss) {
            String rssUri = "http4://" + rss.replaceAll("https?://", "") + "?httpClientConfigurer=httpConfigurer";
            logger.debug("Got URI for polling {}", rssUri);
            return rssUri;
        }

        @Override
        public PollingTaskResult call() throws PollingException, FeedDataException {
            PollingTaskResult result = new PollingTaskResult(rssURI);
            logger.debug("Started polling rss: {} into file: {}", rssURI, path);
            SyndFeed feed = null;
            try {
                ConsumerTemplate rssConsumer = camelContext.createConsumerTemplate();
                InputStream in = rssConsumer.receiveBody(rssURI, 60000, InputStream.class);
                SyndFeedInput feedInput = new SyndFeedInput();
                SyndFeed fullFeed = feedInput.build(new XmlReader(in));
                feed = filterEntriesByDate(fullFeed);
            } catch (Exception e) {
                logger.error("Polling rss {} failed due to error: {}, {}", rss.getRss(), e.getMessage(), e);
                throw new PollingException("Polling RSS " + rss.getRss() + " failed due to error: " + e.getMessage(), e);
            }

            if (feed.getEntries().isEmpty()) {
                logger.error("There are no updates for feed {}", rss.getRss());
                throw new FeedDataException("There are no updates for feed " + rss.getRss());
            }

            logger.debug("Finished polling {}.\nTitle: {}.\nDescription: {}", rssURI, feed.getTitle(), feed.getDescription());

            File folder = new File(path);
            if (!folder.exists() && !folder.mkdirs())
                throw new PollingException("System error: can not create local folder for data files");

            if (folder.exists() && folder.isDirectory()) {
                String file = folder.getPath() + "/" + fileName;
                try (OutputStream out = new FileOutputStream(file)) {
                    out.write(RssConverter.feedToXml(feed).getBytes());
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    throw new PollingException(e.getMessage(), e);
                }
                logger.debug("Feed {} marshaled into file {}", rss.getRss(), file);
                result.setFileName(file);
                result.setStatus(TaskStatus.COMPLETED);
            }
            return result;
        }

        private SyndFeed filterEntriesByDate(SyndFeed feed) {
            LocalDate lastUpdateDate = LocalDate.now().minus(lastUpdateCount, ChronoUnit.valueOf(lastUpdateTimeunit));
            List<SyndEntry> entries = (List) feed.getEntries()
                    .stream()
                    .filter(entry -> ((SyndEntry) entry).getPublishedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isAfter(lastUpdateDate))
                    .collect(Collectors.toList());
            feed.setEntries(entries);
            return feed;
        }

        class PollingTaskResult {
            private TaskStatus status;
            private String fileName;
            private String rss;

            PollingTaskResult(String rss) {
                this.rss = rss;
                this.status = TaskStatus.NOT_STARTED;
            }

            TaskStatus getStatus() {
                return status;
            }

            void setStatus(TaskStatus status) {
                this.status = status;
            }

            String getFileName() {
                return fileName;
            }

            void setFileName(String fileName) {
                this.fileName = fileName;
            }

            String getRss() {
                return rss;
            }

            void setRss(String rss) {
                this.rss = rss;
            }
        }
    }

    class PollingResultHandler implements Runnable {
        private final String username;
        private Subscriber subscriber;
        private Map<Rss, Future<RssPollingTask.PollingTaskResult>> taskMap;


        public PollingResultHandler(String username, Subscriber subscriber, Map<Rss, Future<RssPollingTask.PollingTaskResult>> taskMap) {
            this.username = username;
            this.subscriber = subscriber;
            this.taskMap = taskMap;
        }

        @Override
        public void run() {
            for (Map.Entry<Rss, Future<RssPollingTask.PollingTaskResult>> entry : taskMap.entrySet()) {
                Rss rss = entry.getKey();
                try {
                    RssPollingTask.PollingTaskResult taskResult = entry.getValue().get(60, TimeUnit.SECONDS);
                    if (taskResult.getStatus() == TaskStatus.COMPLETED) {
                        logger.info("RSS has been polled successfully. Username = {}; rss = {}, file = {}", username, rss.getRss(), taskResult.getFileName());
                        rss.setStatus(RssStatus.ACTIVE.toString());
                        rss.setRetryCount((short) 0);
                        rss.setErrorMessage("");
                    } else {
                        logger.error("RSS has not been polled due to undefined error. Username = {}; rss = {}", username, taskResult.getRss());
                        throw new PollingException("RSS " + taskResult.getRss() + " has not been polled due to undefined error");
                    }
                } catch (Exception e) {
                    if (e.getMessage().contains("FeedDataException")) {
                        logger.warn(e.getMessage());
                        rss.setErrorMessage(e.getMessage());
                    } else {
                        logger.error("Error type {}", e.getClass().getCanonicalName());
                        logger.error(e.getMessage(), e);

                        rss.setErrorMessage(e.getMessage());
                        rss.setRetryCount((short) (rss.getRetryCount() + 1));
                        if (rss.getRetryCount() > 4) {
                            rss.setStatus(RssStatus.DEAD.toString());
                        } else {
                            rss.setStatus(RssStatus.OFFLINE.toString());
                        }
                    }
                }
            }
            try {
                subscriberRepository.updateSubscriber(username, subscriber);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }

        }
    }
}
