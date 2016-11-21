package org.roag.camel;

import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.roag.mongo.MongoHelper;
import org.roag.mongo.Rsslist;
import org.roag.mongo.Subscriber;
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

    private SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:MM:ss");
    private Calendar calendar = Calendar.getInstance();

    public RSS2XMLBuilder()
    {

    }

    public void runRssPollingForAllSubscribers() throws Exception
    {
        logger.debug("Something called me runRssPollingForAllSubscribers");
        runRssPolling(mongoHelper.getSubscribers(producerTemplate));
    }

    public void runRssPolling(List<Subscriber> subscriberList) throws Exception
    {
        for (Subscriber subscriber:subscriberList)
            runRssPolling(subscriber);

    }


    private void runRssPolling(Subscriber subscriber) throws Exception
    {

        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - 30);//TODO: -30 is just for testing
        String currentDate = f.format(calendar.getTime());

        for (int i = 0; i < subscriber.getRsslist().size(); i++)
        {
            Rsslist rss = subscriber.getRsslist().get(i);
            if ("active".equals(rss.getStatus()))
            {
                String rssUri = "rss:" + rss.getRss() + "?feedHeader=" + feedHeaders
                        + "&splitEntries=" + splitEntries
                        + "&consumerDelay=" + consumerDelay
                        + "&lastUpdate=" + //TODO: lastupdate for rss
                            /*StringUtils.isNotBlank(rss_lastUpdate)?rss_lastUpdate:*/
                        currentDate;
                String fileUri = "file://" + storagePathRss +
                        subscriber.getEmail() + "/" + rss.getRss().replace('/', '_').replace(":", "_");

                logger.debug("Trying to connect to RSS: {}  and save it to: {}", rssUri, fileUri);

                try
                {
                    getCamelContext().addRoutes(
                            new RSSDynamicRouteBuilder(getCamelContext(),
                                    subscriber.getEmail() + "_" + i,
                                    rssUri,
                                    fileUri));
                }
                catch (CamelException e)
                {
                    logger.error(e.getMessage(), e);
                }

            }
        }

    }

    private static final class RSSDynamicRouteBuilder extends RouteBuilder
    {
        private final String from;
        private final String to;
        private final String sedaQueue;


        private RSSDynamicRouteBuilder(CamelContext context, String sedaQueue, String from, String to)
        {
            super(context);
            logger.debug("RSS dynamic route: seda:{}, {}, {}, {}", sedaQueue, from, to);
            this.sedaQueue = sedaQueue;
            this.from = from;
            this.to = to;
        }

        @Override
        public void configure() throws Exception
        {
            //we use seda:xxx?concurrentConsumers=1 to avoid concurrency issues when several subscribers poll same rss
            from("seda:" + sedaQueue + "?concurrentConsumers=1").
                    log(LoggingLevel.DEBUG, logger, "Polling RSS from " + from + " to " + to).
                    from(from).id(sedaQueue + from).routePolicyRef("rssPolicy").
                    marshal().rss().to(to);
        }
    }

}
