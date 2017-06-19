package org.roag.camel;

import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
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

    private CamelContext camelContext;
    private SubscriberRepository subscriberRepository;

    @Value("${rss.opt.splitEntries}")
    private String splitEntries;

    @Value("${rss.opt.feedHeader}")
    private String feedHeaders;

    @Value("${rss.opt.consumerDelay}")
    private String consumerDelay;

    @Value("${storage.path.rss}")
    private String storagePathRss;

    @Value("${rss.opt.lastUpdate.count}")
    private int lastUpdateCount;

    @Value("${rss.opt.lastUpdate.timeunit}")
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
        runRssPolling(subscriberRepository.findAll());
    }

    public void runRssPolling(List<Subscriber> subscriberList) throws Exception
    {
        for (Subscriber subscriber:subscriberList)
            runRssPolling(subscriber);
    }


    public void runRssPolling(String email) throws Exception
    {
        logger.debug("Start polling RSS-feed for {}", email);
        runRssPolling(subscriberRepository.getSubscriber(email));
    }

    public void runRssPolling(Subscriber subscriber) throws Exception
    {
        logger.debug("Start polling RSS-feed for subscriber: email = {}; name = {}", subscriber.getEmail(), subscriber.getName());
        for (int i = 0; i < subscriber.getRsslist().size(); i++)
        {
            Rss rss = subscriber.getRsslist().get(i);
            if (RssStatus.ACTIVE == RssStatus.fromValue(rss.getStatus()))
            {
                try
                {
                    camelContext.addRoutes(
                            new RSSDynamicRouteBuilder(camelContext,
                                    subscriber.getEmail() + "_" + i,
                                    subscriber.getEmail(),
                                    subscriber.getName(),
                                    rss.getRss()));
                }
                catch (CamelException e)
                {
                    logger.error(e.getMessage(), e);
                }

            }
        }
    }

    private final class RSSDynamicRouteBuilder extends RouteBuilder
    {
        private final String rss;
        private final String email;
        private final String sedaQueue;
        private final String name;
        private final String fileName;

        private Calendar calendar = Calendar.getInstance();

        private RSSDynamicRouteBuilder(CamelContext context, String sedaQueue, String email, String name, String rss)
        {
            super(context);
            logger.debug("Creation of dynamic route: seda:{}, from {} for email {}", sedaQueue, rss, email);
            this.sedaQueue = sedaQueue;
            this.rss = rss;
            this.name = name;
            this.email = email;
            this.fileName = getRssFileNameFormat().format(new Date());
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
                    + "&consumerDelay=" + consumerDelay
                    + "&lastUpdate=" + lastUpdate;
            return rssUri;
        }

        private String getAbsoluteCamelFileUri(String email, String rss)
        {
            String fileUri = "file://" + storagePathRss +
                    email + "/"
                    + rss.replace('/', '_').replace(":", "_")
                    + "?fileName=" + fileName;
            return fileUri;
        }

        private String getRelativeFilePath(String email, String rss)
        {
            String fileUri =
                    email + "/"
                    + rss.replace('/', '_').replace(":", "_")
                    + "/" + fileName;
            return fileUri;
        }

        @Override
        public void configure() throws Exception
        {
//            errorHandler(deadLetterChannel("seda:errors").
//                    maximumRedeliveries(2).redeliveryDelay(4000).
//                    retryAttemptedLogLevel(LoggingLevel.ERROR));

            onException(RuntimeException.class).maximumRedeliveries(2).handled(true).
                    log(LoggingLevel.ERROR, logger,"jopa");
//          TODO: we use seda:xxx?concurrentConsumers=1 to avoid concurrency issues when several subscribers poll same rss, but it does not work. We need queue
            from("seda:" + sedaQueue + "?concurrentConsumers=1").
                    setHeader("email").constant(email).
                    setHeader("name").constant(name).
                    setHeader("rss").constant(rss).
                    setHeader("CamelFileName").constant(getRelativeFilePath(email, rss)).
                    log(LoggingLevel.DEBUG, logger, "seda:" + sedaQueue + " - Start polling RSS from " + getCamelRssUri(rss) + " to " + getAbsoluteCamelFileUri(email, rss)).
                    from(getCamelRssUri(rss)).
                    //id(sedaQueue + rss).
                    routePolicyRef("rssPolicy").
                        marshal().rss().to(getAbsoluteCamelFileUri(email, rss));
//                        to("direct:startTransformation");
        }
    }
}
