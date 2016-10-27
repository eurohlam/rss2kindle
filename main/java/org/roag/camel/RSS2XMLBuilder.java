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
//@DependsOn({"mongoBean", "mongoHelper"})
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

    @PropertyInject("mongodb.subscribers.filter")
    private String subscribersFilter;

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
        runRssPolling(mongoHelper.getSubscribers(producerTemplate));
    }

    public void runRssPolling(List<Subscriber> subscriberList) throws Exception
    {
        for (int i=0; i<subscriberList.size(); i++)
            runRssPolling(subscriberList.get(i), i);

    }

    public void runRssPolling(Subscriber subscriber) throws Exception
    {
        runRssPolling(subscriber, -1);
    }


    private void runRssPolling(Subscriber subscriber, int startupOrder) throws Exception
    {

        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - 30);//TODO: -30 is just for testing
        String currentDate = f.format(calendar.getTime());

        for (int i=0; i<subscriber.getRsslist().size(); i++)
        {
            Rsslist rss=subscriber.getRsslist().get(i);
            if ("active".equals(rss.getStatus()))
            {
                String rssUri="rss:" + rss.getRss() + "?feedHeader=" + feedHeaders
                        + "&splitEntries=" + splitEntries
                        + "&consumerDelay=" + consumerDelay
                        + "&lastUpdate=" +
                            /*StringUtils.isNotBlank(rss_lastUpdate)?rss_lastUpdate:*/
                        currentDate;
                String fileUri = "file://" + storagePathRss +
                        subscriber.getEmail() + "/" + rss.getRss().replace('/', '_').replace(":","_");

                logger.debug("Trying to connect to RSS: {}  and save it to: {}", rssUri, fileUri);

                try
                {
                    getCamelContext().addRoutes(
                            new RSSDynamicRouteBuilder(getCamelContext(),
                            subscriber.getEmail()+"_"+i,
                            rssUri,
                            fileUri,
                            startupOrder*10 + i));//we need to send startupOrder to avoid polling rss from same endpoint in parallel
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
        private final String from;
        private final String to;
        private final String direct;
        private final int startupOrder;

        private RSSDynamicRouteBuilder(CamelContext context, String direct, String from, String to)
        {
            this(context, direct, from, to, -1);
        }

        private RSSDynamicRouteBuilder(CamelContext context, String direct, String from, String to, int startupOrder)
        {
            super(context);
            logger.debug("RSS dynamic route: direct:{}, {}, {}, {}", direct, from, to, startupOrder);
            this.direct = direct;
            this.from = from;
            this.to = to;
            this.startupOrder = startupOrder;
        }

        @Override
        public void configure() throws Exception
        {
            if (startupOrder == -1)
                from("direct:" + direct).log(LoggingLevel.DEBUG, logger, "Polling RSS from "+from + " to "+to).
                    from(from).routePolicyRef("rssPolicy").
                    marshal().rss().to(to);
            else
                from("direct:" + direct).log(LoggingLevel.DEBUG, logger, "Polling RSS from "+from + " to "+to).
                        startupOrder(startupOrder);
                from(from).routePolicyRef("rssPolicy").
                        marshal().rss().to(to);

        }
    }

}
