package org.roag.camel;

import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.roag.mongo.MongoHelper;
import org.roag.mongo.Rsslist;
import org.roag.mongo.Subscriber;
import org.roag.mongo.SubscriberFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;

import static org.roag.camel.ServiceLocator.*;

/**
 * Created by eurohlam on 03.10.16.
 */
@Component
@SuppressWarnings("unused")
public class RSS2XMLBuilder
{
    final public static Logger logger = LoggerFactory.getLogger(RSS2XMLBuilder.class);

    @Autowired
    private MongoHelper mongoHelper;

    @PropertyInject("rss.opt.splitEntries")
    private String splitEntries;

    @PropertyInject("rss.opt.feedHeader")
    private String feedHeaders;

    @PropertyInject("rss.opt.consumerDelay")
    private String consumerDelay;

    @PropertyInject("storage.path.rss")
    private String storagePathRss;

    @Produce
    private ProducerTemplate producerTemplate;

    @Consume
    private ConsumerTemplate consumerTemplate;


    public RSS2XMLBuilder()
    {

    }

    public void runRssPollingForAllSubscribers() throws Exception
    {
        logger.debug("Start polling RSS-feeds for all active subscribers");
        runRssPolling(mongoHelper.getSubscribers(producerTemplate));
    }

    public void runRssPolling(List<Subscriber> subscriberList) throws Exception
    {
        for (Subscriber subscriber:subscriberList)
            runRssPolling(subscriber);
    }


    public void runRssPolling(Subscriber subscriber) throws Exception
    {
        logger.debug("Start polling RSS-feed for " + subscriber.getEmail());
        for (int i = 0; i < subscriber.getRsslist().size(); i++)
        {
            Rsslist rss = subscriber.getRsslist().get(i);
            if (SubscriberFactory.RSS_STATUS.ACTIVE.toString().equals(rss.getStatus()))
            {
                try
                {
                    getCamelContext().addRoutes(
                            new RSSDynamicRouteBuilder(getCamelContext(),
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
            SimpleDateFormat format = new SimpleDateFormat("yyMMddHHmmss");
            fileName = format.format(new Date());
        }

        private String getCamelRssUri(String rss)
        {
            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - 30);//TODO: -30 is just for testing
            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:MM:ss");
            String currentDate = f.format(calendar.getTime());
            String rssUri = "rss:" + rss + "?feedHeader=" + feedHeaders
                    + "&splitEntries=" + splitEntries
                    + "&consumerDelay=" + consumerDelay
                    + "&lastUpdate=" + //TODO: lastupdate for rss
                            /*StringUtils.isNotBlank(rss_lastUpdate)?rss_lastUpdate:*/
                    currentDate;
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
            //we use seda:xxx?concurrentConsumers=1 to avoid concurrency issues when several subscribers poll same rss
            from("seda:" + sedaQueue + "?concurrentConsumers=1").
                    setHeader("email").constant(email).
                    setHeader("name").constant(name).
                    setHeader("rss").constant(rss).
                    setHeader("CamelFileName").constant(getRelativeFilePath(email, rss)).
                    log(LoggingLevel.DEBUG, logger, "seda:" + sedaQueue + " - Start polling RSS from " + getCamelRssUri(rss) + " to " + getAbsoluteCamelFileUri(email, rss)).
                    from(getCamelRssUri(rss)).id(sedaQueue + rss).routePolicyRef("rssPolicy").
                    marshal().rss().to(getAbsoluteCamelFileUri(email, rss)).
                    to("direct:startTransformation");
        }
    }
}
