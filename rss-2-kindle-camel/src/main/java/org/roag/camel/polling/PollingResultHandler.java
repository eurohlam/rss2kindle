package org.roag.camel.polling;

import org.roag.camel.PollingException;
import org.roag.ds.SubscriberRepository;
import org.roag.model.Rss;
import org.roag.model.RssStatus;
import org.roag.model.Subscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by eurohlam on 9/08/2019.
 */
public class PollingResultHandler implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(PollingResultHandler.class);

    private SubscriberRepository subscriberRepository;
    private final String username;
    private Subscriber subscriber;
    private Map<Rss, Future<PollingTaskResult>> taskMap;


    public PollingResultHandler(SubscriberRepository subscriberRepository, String username, Subscriber subscriber,
                                Map<Rss, Future<PollingTaskResult>> taskMap) {
        this.subscriberRepository = subscriberRepository;
        this.username = username;
        this.subscriber = subscriber;
        this.taskMap = taskMap;
    }

    @Override
    public void run() {
        for (Map.Entry<Rss, Future<PollingTaskResult>> entry : taskMap.entrySet()) {
            Rss rss = entry.getKey();
            try {
                PollingTaskResult taskResult = entry.getValue().get(60, TimeUnit.SECONDS);
                if (taskResult.getStatus() == TaskStatus.COMPLETED) {
                    logger.info("RSS has been polled successfully. Username = {}; rss = {}, file = {}",
                            username, rss.getRss(), taskResult.getFileName());
                    rss.setStatus(RssStatus.ACTIVE.toString());
                    rss.setRetryCount((short) 0);
                    rss.setErrorMessage("");
                } else {
                    logger.error("RSS has not been polled due to undefined error. Username = {}; rss = {}",
                            username, taskResult.getRss());
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
