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
@Scope("singleton")
@SuppressWarnings("unused")
public class Rss2XmlHandler
{
    final private static Logger logger = LoggerFactory.getLogger(Rss2XmlHandler.class);

    private Calendar calendar = Calendar.getInstance();

    private CamelContext camelContext;
    private SubscriberRepository subscriberRepository;
    private ConsumerTemplate rssConsumer;
    private ExecutorService executor;

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

    private static final ThreadLocal<DateFormat> RSS_LAST_UPDATE_FORMAT = new ThreadLocal<DateFormat>(){
        @Override
        protected DateFormat initialValue() {
            return  new SimpleDateFormat("yyyy-MM-dd'T'HH:MM:ss");
        }
    };

    private static final ThreadLocal<DateFormat> RSS_FILE_NAME_FORMAT = new ThreadLocal<DateFormat>(){
        @Override
        protected DateFormat initialValue() {
            return  new SimpleDateFormat("yyMMddHHmmss");
        }
    };

    @Autowired
    public Rss2XmlHandler(@Qualifier("subscriberRepository") SubscriberRepository subscriberRepository,
                          @Qualifier("mainCamelContext") CamelContext camelContext)
    {
        this.subscriberRepository = subscriberRepository;
        this.camelContext = camelContext;
        this.rssConsumer = camelContext.createConsumerTemplate();
        this.executor = Executors.newCachedThreadPool();//TODO: may be better to use pool
    }

    public static DateFormat getRssLastUpdateFormat()
    {
        return RSS_LAST_UPDATE_FORMAT.get();
    }

    public static DateFormat getRssFileNameFormat()
    {
        return RSS_FILE_NAME_FORMAT.get();
    }


    public void runRssPollingForAllUsers() throws Exception
    {
        logger.debug("Start polling RSS for all active users");
        for (User user: subscriberRepository.findAll())
            if (UserStatus.fromValue(user.getStatus()) == UserStatus.ACTIVE)
                runRssPollingForList(user.getSubscribers());
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

    public void runRssPollingForList(List<Subscriber> subscriberList) throws Exception
    {
        for (Subscriber subscriber:subscriberList)
            runRssPollingForSubscriber(subscriber);
    }


    public short runRssPollingForSubscriber(String username, String email) throws Exception
    {
        return runRssPollingForSubscriber(subscriberRepository.getSubscriber(username, email));
    }

    public short runRssPollingForSubscriber(Subscriber subscriber) throws Exception
    {
        short count= 0;
        logger.debug("Start polling RSS for subscriber: email = {}; name = {}", subscriber.getEmail(), subscriber.getName());
        for (int i = 0; i < subscriber.getRsslist().size(); i++)
        {
            Rss rss = subscriber.getRsslist().get(i);
            if (RssStatus.DEAD != RssStatus.fromValue(rss.getStatus()))
            {
                try
                {
                    logger.info("Started polling RSS {}", rss.getRss());
                    Future<Map<String, String>> result= executor.submit(new RssPollingTask(rssConsumer,getCamelRssUri(rss.getRss()),
                            getPathForRss(subscriber.getEmail(),
                                    rss.getRss()),getRssFileNameFormat().format(new Date())));
                    if (result.isDone())
                    {
                        Map<String, String> map = result.get();
                        if (map.size()==1)
                        {
                            logger.info("RSS has been polled successfully to {}", map.get(rss.getRss()));
                            count++;
                        }
                        else
                        {
                            logger.error("RSS has not been polled due to error {}", rss.getRss());
                            throw new Exception("RSS " + rss.getRss() +" has not been polled");
                        }
                    }
                } catch (Exception e)
                {
                    logger.error(e.getMessage());
                    //TODO: change status of failed rss
                }

            }
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
        } else if (TimeUnit.valueOf(lastUpdateTimeunit) == TimeUnit.HOURS)
        {
            calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - lastUpdateCount);
            lastUpdate = getRssLastUpdateFormat().format(calendar.getTime());
        } else
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

    private String getPathForRss(String email, String rss)
    {
        String fileUri = storagePathRss +
                email + "/"
                + rss.replace('/', '_').replace(":", "_");
        return fileUri;
    }
}
