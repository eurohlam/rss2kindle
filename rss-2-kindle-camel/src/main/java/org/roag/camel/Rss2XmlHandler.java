package org.roag.camel;

import org.apache.camel.*;
import org.roag.ds.SubscriberRepository;
import org.roag.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


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
        Future<RssPollingTask.PollingTaskResult> result = taskExecutor.submit(new RssPollingTask(rssConsumer, getCamelRssUri(rss),
                getPathForRss("guest", email, rss), getRssFileNameFormat().format(new Date())));
        logger.info("Anonymous polling for email {} from rss {} is completed with result {}", email, rss, result.get().getStatus());

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
    public int runRssPollingForList(String username, List<Subscriber> subscriberList) throws Exception
    {
        int count = 0;
        for (Subscriber subscriber : subscriberList)
            count = count + runRssPollingForSubscriber(username, subscriber);
        return count;
    }


    /**
     * @param username
     * @param email
     * @return number of successfully polled subscriptions
     * @throws Exception
     */
    public short runRssPollingForSubscriber(String username, String email) throws Exception
    {
        return runRssPollingForSubscriber(username, subscriberRepository.getSubscriber(username, email));
    }

    /**
     * @param username
     * @param subscriber
     * @return number of successfully polled subscriptions
     * @throws Exception
     */
    public short runRssPollingForSubscriber(String username, Subscriber subscriber) throws Exception
    {
        short count = 0;
        logger.debug("Start polling: user = {}; subscriber = {}; subscriber.name = {}", username, subscriber.getEmail(), subscriber.getName());
        for (int i = 0; i < subscriber.getRsslist().size(); i++)
        {
            Rss rss = subscriber.getRsslist().get(i);
            if (RssStatus.DEAD != RssStatus.fromValue(rss.getStatus()))
            {
                logger.info("Polling RSS {}: user={}; subscriber = {}", rss.getRss(), username, subscriber.getEmail());
                rss.setLastPollingDate(dateFormat.get().format(new Date()));
                try
                {
                    Future<RssPollingTask.PollingTaskResult> result = taskExecutor.submit(new RssPollingTask(rssConsumer, getCamelRssUri(rss.getRss()),
                            getPathForRss(username, subscriber.getEmail(),
                                    rss.getRss()), getRssFileNameFormat().format(new Date())));

                    TaskResultHandler resultHandler = new TaskResultHandler(result, username, rss);
                    resultExecutor.submit(resultHandler);
/*
                    RssPollingTask.PollingTaskResult taskResult = result.get(60, TimeUnit.SECONDS);
                    //TODO: it is blocking execution now, need to think about unblocked execution
                    if (taskResult.getStatus() == RssPollingTask.TaskStatus.COMPLETED)
                    {
                        logger.info("RSS {} has been polled successfully to {}; user = {}; subscriber = {}", rss.getRss(), taskResult.getFileName(), username, subscriber.getEmail());
                        rss.setStatus(RssStatus.ACTIVE.toString());
                        rss.setRetryCount((short)0);
                        rss.setErrorMessage("");
                        count++;
                    }
                    else
                    {
                        logger.error("RSS {} has not been polled due to undefined error.  username = {}; subscriber = {}", rss.getRss(), username, subscriber.getEmail());
                        throw new Exception("RSS " + rss.getRss() + " has not been polled");
                    }
*/
                }
                catch (Exception e)
                {
                    logger.error(e.getMessage(), e);

/*
                    rss.setErrorMessage(e.getMessage());
                    rss.setRetryCount((short)(rss.getRetryCount() + 1));
                    if (rss.getRetryCount() > 4)
                        rss.setStatus(RssStatus.DEAD.toString());
                    else
                        rss.setStatus(RssStatus.OFFLINE.toString());
*/
                }

            }
            subscriberRepository.updateSubscriber(username, subscriber);
        }
        return count;
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

    private class TaskResultHandler implements Runnable
    {
        private Future<RssPollingTask.PollingTaskResult> future;
        private Rss rss;
        private String username;

        public TaskResultHandler(Future<RssPollingTask.PollingTaskResult> future, String username, Rss rss)
        {
            this.future = future;
            this.rss = rss;
            this.username = username;
        }

        @Override
        public void run()
        {
            try
            {
                RssPollingTask.PollingTaskResult taskResult = future.get(60, TimeUnit.SECONDS);
                if (taskResult.getStatus() == RssPollingTask.TaskStatus.COMPLETED)
                {
                    logger.info("RSS {} has been polled successfully to {}; user = {};", rss.getRss(), taskResult.getFileName(), username);
                    rss.setStatus(RssStatus.ACTIVE.toString());
                    rss.setRetryCount((short)0);
                    rss.setErrorMessage("");
                }
                else
                {
                    logger.error("RSS {} has not been polled due to undefined error.  username = {};", rss.getRss(), username);
                    throw new Exception("RSS " + rss.getRss() + " has not been polled");
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
    }
}
