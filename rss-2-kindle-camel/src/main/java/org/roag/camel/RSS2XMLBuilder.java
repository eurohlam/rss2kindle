package org.roag.camel;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.rss.RssConverter;
import org.roag.ds.SubscriberRepository;
import org.roag.model.Rss;
import org.roag.model.RssStatus;
import org.roag.model.Subscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;


/**
 * Created by eurohlam on 03.10.16.
 */
@Component
@Scope("singleton")
@SuppressWarnings("unused")
public class RSS2XMLBuilder
{
    final private static Logger logger = LoggerFactory.getLogger(RSS2XMLBuilder.class);

    private Calendar calendar = Calendar.getInstance();

    private CamelContext camelContext;
    private SubscriberRepository subscriberRepository;
    private ConsumerTemplate rssConsumer;

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
    public RSS2XMLBuilder(@Qualifier("subscriberRepository") SubscriberRepository subscriberRepository,
                          @Qualifier("mainCamelContext") CamelContext camelContext)
    {
        this.subscriberRepository = subscriberRepository;
        this.camelContext = camelContext;
        this.rssConsumer = camelContext.createConsumerTemplate();
    }

    public static DateFormat getRssLastUpdateFormat()
    {
        return RSS_LAST_UPDATE_FORMAT.get();
    }

    public static DateFormat getRssFileNameFormat()
    {
        return RSS_FILE_NAME_FORMAT.get();
    }

    public void runRssPollingForAllSubscribers() throws Exception
    {
        logger.debug("Start polling RSS-feeds for all active subscribers");
        runRssPollingForList(subscriberRepository.findAll());
    }

    public void runRssPollingForList(List<Subscriber> subscriberList) throws Exception
    {
        for (Subscriber subscriber:subscriberList)
            runRssPollingForSubscriber(subscriber);
    }


    public void runRssPollingForSubscriber(String email) throws Exception
    {
        logger.debug("Start polling RSS-feed for {}", email);
        runRssPollingForSubscriber(subscriberRepository.getSubscriber(email));
    }

    public void runRssPollingForSubscriber(Subscriber subscriber) throws Exception
    {
        logger.debug("Start polling RSS-feed for subscriber: email = {}; name = {}", subscriber.getEmail(), subscriber.getName());
        for (int i = 0; i < subscriber.getRsslist().size(); i++)
        {
            Rss rss = subscriber.getRsslist().get(i);
            if (RssStatus.ACTIVE == RssStatus.fromValue(rss.getStatus()))
            {
                try
                {
                    logger.debug("Polling {}", getCamelRssUri(rss.getRss()));
                    SyndFeed feed = rssConsumer.receiveBody(getCamelRssUri(rss.getRss()), SyndFeedImpl.class);
                    logger.error(feed.getTitle() + "  " + feed.getDescription());
                    logger.error(RssConverter.feedToXml(feed));
                    File file=new File(getAbsoluteCamelFileUri(subscriber.getEmail(), rss.getRss()));
                    if (!file.exists())
                        file.mkdirs();
                    if (file.exists() && file.isDirectory())
                    {
                        OutputStream out = new FileOutputStream(file.getPath() + "/" + getRssFileNameFormat().format(new Date()));
                        out.write(RssConverter.feedToXml(feed).getBytes());
                        out.close();
                    }
                } catch (Exception e)
                {
                    logger.error(e.getMessage());
                    //TODO: change status of failed rss
                }

            }
        }
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

    private String getAbsoluteCamelFileUri(String email, String rss)
    {
        String fileUri = /*"file://" +*/ storagePathRss +
                email + "/"
                + rss.replace('/', '_').replace(":", "_");
//                + "?fileName="
//                + getRssFileNameFormat().format(new Date());
        return fileUri;
    }
}
