package org.roag.camel;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import org.apache.camel.*;
import org.apache.camel.dataformat.rss.RssConverter;
import org.roag.ds.SubscriberRepository;
import org.roag.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ConnectException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;


/**
 * Created by eurohlam on 03.10.16.
 */
@Component
@Scope("prototype")  //TODO: can be a cause of concurrency issues or memeory leak
@SuppressWarnings("unused")
public class Rss2XmlHandler
{
    final private static Logger logger = LoggerFactory.getLogger(Rss2XmlHandler.class);

    private Calendar calendar = Calendar.getInstance();

    private CamelContext camelContext;
    private SubscriberRepository subscriberRepository;
    private ConsumerTemplate rssConsumer;
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

    private static final ThreadLocal<DateFormat> RSS_LAST_UPDATE_FORMAT = new ThreadLocal<DateFormat>()
    {
        @Override
        protected DateFormat initialValue()
        {
            return new SimpleDateFormat("yyyy-MM-dd'T'HH:MM:ss");
        }
    };

    private static final ThreadLocal<DateFormat> RSS_FILE_NAME_FORMAT = new ThreadLocal<DateFormat>()
    {
        @Override
        protected DateFormat initialValue()
        {
            return new SimpleDateFormat("yyMMddHHmmss");
        }
    };

    private static final ThreadLocal<DateFormat> dateFormat = new ThreadLocal<DateFormat>(){
        @Override
        protected SimpleDateFormat initialValue()
        {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };

    @Autowired
    public Rss2XmlHandler(@Qualifier("subscriberRepository") SubscriberRepository subscriberRepository,
                          @Qualifier("mainCamelContext") CamelContext camelContext)
    {
        this.subscriberRepository = subscriberRepository;
        this.camelContext = camelContext;
        this.rssConsumer = camelContext.createConsumerTemplate();
        this.taskExecutor = Executors.newFixedThreadPool(10);
        this.resultExecutor = Executors.newFixedThreadPool(10);
    }

    public static DateFormat getRssLastUpdateFormat()
    {
        return RSS_LAST_UPDATE_FORMAT.get();
    }

    public static DateFormat getRssFileNameFormat()
    {
        return RSS_FILE_NAME_FORMAT.get();
    }


    public void runCustomRssPolling(String email, String rss) throws Exception
    {
        logger.info("Anonymous polling for email {} from rss {}", email, rss);
        throw new IllegalArgumentException("Not implemented yet"); //TODO:
//        Future<RssPollingTask.PollingTaskResult> result = taskExecutor.submit(new RssPollingTask(rssConsumer, getCamelRssUri(rss),
//                getPathForRss("guest", email, rss), getRssFileNameFormat().format(new Date())));
//        logger.info("Anonymous polling for email {} from rss {} is completed with result {}", email, rss, result.get().getStatus());

    }

    public void runRssPollingForAllUsers() throws Exception
    {
        logger.debug("Start polling for all active users");
        for (User user : subscriberRepository.getUserRepository().findAll())
            if (UserStatus.fromValue(user.getStatus()) == UserStatus.ACTIVE)
                runRssPollingForList(user.getUsername(), user.getSubscribers());
            else
                logger.debug("User {} is locked and will not be processed", user.getUsername());
    }


/*
    public void runRssPollingForAllSubscribers() throws Exception
    {
        logger.debug("Start polling RSS for all active subscribers");
        runRssPollingForList(subscriberRepository.findAll());
    }
*/

    /**
     * @param username
     * @param subscriberList
     * @return number of successfully polled subscriptions
     * @throws Exception
     */
    public void runRssPollingForList(String username, List<Subscriber> subscriberList) throws Exception
    {
        for (Subscriber subscriber : subscriberList)
            runRssPollingForSubscriber(username, subscriber);
    }


    /**
     * @param username
     * @param email
     * @return number of successfully polled subscriptions
     * @throws Exception
     */
    public void runRssPollingForSubscriber(String username, String email) throws Exception
    {
        runRssPollingForSubscriber(username, subscriberRepository.getSubscriber(username, email));
    }

    /**
     * @param username
     * @param subscriber
     * @return number of successfully polled subscriptions
     * @throws Exception
     */
    public void runRssPollingForSubscriber(String username, Subscriber subscriber) throws Exception
    {
        logger.debug("Start polling: user = {}; subscriber = {}; subscriber.name = {}", username, subscriber.getEmail(), subscriber.getName());
        Map<Rss, Future<RssPollingTask.PollingTaskResult>> taskMap=new HashMap<>(subscriber.getRsslist().size());
        //run polling rss asynchronously
        for (int i = 0; i < subscriber.getRsslist().size(); i++)
        {
            Rss rss = subscriber.getRsslist().get(i);
            if (RssStatus.DEAD != RssStatus.fromValue(rss.getStatus()))
            {
                logger.info("Polling RSS {}: user={}; subscriber = {}", rss.getRss(), username, subscriber.getEmail());
                rss.setLastPollingDate(dateFormat.get().format(new Date()));
                try
                {
                    Future<RssPollingTask.PollingTaskResult> result = taskExecutor.submit(
                            new RssPollingTask(rss, getPathForRss(username, subscriber.getEmail(), rss.getRss())));
                    taskMap.put(rss, result);

                }
                catch (Exception e)
                {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        //check polling rss results
        resultExecutor.submit(new PollingResultHandler(username, subscriber, taskMap));
    }

    private String getCamelRssUri(String rss)
    {
        String lastUpdate;
        if (TimeUnit.valueOf(lastUpdateTimeunit) == TimeUnit.DAYS)
        {
            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - lastUpdateCount);
            lastUpdate = getRssLastUpdateFormat().format(calendar.getTime());
        }
        else if (TimeUnit.valueOf(lastUpdateTimeunit) == TimeUnit.HOURS)
        {
            calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - lastUpdateCount);
            lastUpdate = getRssLastUpdateFormat().format(calendar.getTime());
        }
        else
        {
            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - 1);
            lastUpdate = getRssLastUpdateFormat().format(calendar.getTime());
        }

        String rssUri = "rss:" + rss + "?feedHeader=" + feedHeaders
                + "&consumer.bridgeErrorHandler=true"
                + "&splitEntries=" + splitEntries
                + "&consumer.delay=" + consumerDelay
                + "&lastUpdate=" + lastUpdate;
        return rssUri;
    }

    private String getPathForRss(String username, String email, String rss)
    {
        String fileUri = storagePathRss +
                username + "/" +
                email + "/"
                + rss.replace('/', '_').replace(":", "_");
        return fileUri;
    }


    private enum TaskStatus {NOT_STARTED, COMPLETED};

    class RssPollingTask implements Callable<RssPollingTask.PollingTaskResult>
    {

        private Rss rss;
        final private String rssURI;
        final private String path;
        final private String fileName;

        RssPollingTask(Rss rss, String path)
        {
            this.rss = rss;
            this.rssURI = getCamelRssUri(rss.getRss());
            this.path = path;
            this.fileName =  getRssFileNameFormat().format(new Date());
        }

        @Override
        public PollingTaskResult call() throws Exception
        {
            PollingTaskResult result=new PollingTaskResult(rssURI);
            logger.debug("Started polling {}", rssURI);
            SyndFeed feed = null;
            try
            {
                feed = rssConsumer.receiveBody(rssURI, 60000, SyndFeedImpl.class);
            } catch (RuntimeException e)
            {
                logger.error("Polling RSS {} failed due to error: {}, {}", rssURI, e.getMessage(), e);
                throw new ConnectException("Polling RSS " + rssURI +" failed due to error: " + e.getMessage());
            }

            if (feed == null){
                logger.error("Timeout error during the polling {}", rssURI);
                throw new TimeoutException("Timeout error during the polling " + rssURI);
            }
            logger.debug("Finished polling {}.\nTitle: {}.\nDescription: {}", rssURI, feed.getTitle(), feed.getDescription());

            File folder=new File(path);
            if (!folder.exists())
                folder.mkdirs();
            if (folder.exists() && folder.isDirectory())
            {
                String file=folder.getPath() + "/" + fileName;
                OutputStream out = null;
                try {
                    out = new FileOutputStream(file);
                    out.write(RssConverter.feedToXml(feed).getBytes());
                } catch (IOException e)
                {
                    logger.error(e.getMessage(), e);
                    throw e;
                } finally {
                    if (out != null)
                        out.close();
                }
                logger.debug("Feed {} marshaled into file {}", rssURI, file);
                result.setFileName(file);
                result.setStatus(TaskStatus.COMPLETED);
            }
            return result;
        }

        protected class PollingTaskResult
        {
            private TaskStatus status;
            private String fileName;
            private String rss;

            public PollingTaskResult(String rss)
            {
                this.rss = rss;
                this.status = TaskStatus.NOT_STARTED;
            }

            public TaskStatus getStatus()
            {
                return status;
            }

            public void setStatus(TaskStatus status)
            {
                this.status = status;
            }

            public String getFileName()
            {
                return fileName;
            }

            public void setFileName(String fileName)
            {
                this.fileName = fileName;
            }

            public String getRss()
            {
                return rss;
            }

            public void setRss(String rss)
            {
                this.rss = rss;
            }
        }
    }

    class PollingResultHandler implements Runnable
    {
        final private String username;
        private Subscriber subscriber;
        private Map<Rss, Future<RssPollingTask.PollingTaskResult>> taskMap;


        public PollingResultHandler(String username, Subscriber subscriber, Map<Rss, Future<RssPollingTask.PollingTaskResult>> taskMap)
        {
            this.username = username;
            this.subscriber = subscriber;
            this.taskMap = taskMap;
        }

        @Override
        public void run()
        {
            for (Rss rss:taskMap.keySet())
            {
                try
                {
                    RssPollingTask.PollingTaskResult taskResult = taskMap.get(rss).get(60, TimeUnit.SECONDS);
                    if (taskResult.getStatus() == TaskStatus.COMPLETED)
                    {
                        logger.info("RSS {} has been polled successfully to {}; user = {};", rss.getRss(), taskResult.getFileName(), username);
                        rss.setStatus(RssStatus.ACTIVE.toString());
                        rss.setRetryCount((short)0);
                        rss.setErrorMessage("");
                    }
                    else
                    {
                        logger.error("RSS {} has not been polled due to undefined error.  username = {};", taskResult.getRss(), username);
                        throw new Exception("RSS " + taskResult.getRss() + " has not been polled");
                    }
                }
                catch (Exception e)
                {
                    logger.error(e.getMessage(), e);

                    rss.setErrorMessage(e.getMessage());
                    rss.setRetryCount((short)(rss.getRetryCount() + 1));
                    if (rss.getRetryCount() > 4)
                        rss.setStatus(RssStatus.DEAD.toString());
                    else
                        rss.setStatus(RssStatus.OFFLINE.toString());
                }
            }
            try
            {
                subscriberRepository.updateSubscriber(username, subscriber);
            } catch (Exception e)
            {
                logger.error(e.getMessage(), e);
            }

        }
    }
}
