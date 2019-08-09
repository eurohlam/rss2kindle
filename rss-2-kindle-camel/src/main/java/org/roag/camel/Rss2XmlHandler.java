package org.roag.camel;

import org.apache.camel.*;
import org.roag.camel.polling.PollingResultHandler;
import org.roag.camel.polling.PollingTaskResult;
import org.roag.camel.polling.PollingTask;
import org.roag.ds.SubscriberRepository;
import org.roag.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;


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
        for (User user : subscriberRepository.getUserRepository().findAll()) {
            if (UserStatus.fromValue(user.getStatus()) == UserStatus.ACTIVE) {
                runRssPollingForList(user.getUsername(), user.getSubscribers());
            } else {
                logger.info("User {} is locked and will not be processed", user.getUsername());
            }
        }
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
     * @throws Exception - if subscriber has not been found
     */
    public void runRssPollingForSubscriber(String username, String email) throws Exception {
        runRssPollingForSubscriber(username, subscriberRepository.getSubscriber(username, email));
    }

    /**
     * @param username   - unique name of user
     * @param subscriber - subscriber that belongs to current user
     */
    public void runRssPollingForSubscriber(String username, Subscriber subscriber) {
        logger.debug("Initiated polling for: username = {}; subscriber = {}; subscriber.name = {}",
                username, subscriber.getEmail(), subscriber.getName());
        Map<Rss, Future<PollingTaskResult>> taskMap = new HashMap<>(subscriber.getRsslist().size());
        //run polling rss asynchronously
        for (int i = 0; i < subscriber.getRsslist().size(); i++) {
            Rss rss = subscriber.getRsslist().get(i);
            if (RssStatus.DEAD != RssStatus.fromValue(rss.getStatus())) {
                logger.info("Got task for username={}; subscriber = {}, rss = {}", username, subscriber.getEmail(), rss.getRss());
                rss.setLastPollingDate(LocalDateTime.now().toString());
                try {
                    Future<PollingTaskResult> result = taskExecutor.submit(
                            new PollingTask(camelContext.createConsumerTemplate(),
                                    rss,
                                    getPathForRss(username, subscriber.getEmail(), rss.getRss()),
                                    lastUpdateCount,
                                    lastUpdateTimeunit));
                    taskMap.put(rss, result);

                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        //check polling rss results
        resultExecutor.submit(new PollingResultHandler(subscriberRepository, username, subscriber, taskMap));
    }


    private String getPathForRss(String username, String email, String rss) {
        String fileUri = storagePathRss +
                username + "/" +
                email + "/"
                + rss.replace('/', '_').replace(":", "_");
        logger.debug("Got URI for file {}", fileUri);
        return fileUri;
    }
}
