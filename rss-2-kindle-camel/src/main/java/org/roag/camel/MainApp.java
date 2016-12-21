package org.roag.camel;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.spring.Main;

/**
 * This main run once rss-2-kindle process for all active subscribers
 */
public class MainApp
{

    /**
     * A main() so we can easily run these routing rules in our IDE
     */
    public static void main(String... args) throws Exception
    {
        Main main = new Main();
        main.addRouteBuilder(
                new RouteBuilder()
                {
                    @Override
                    public void configure() throws Exception
                    {
                        from("timer://runOnce?repeatCount=1&delay=5000").
                                log(LoggingLevel.INFO, "org.roag.camel.Main", "Run RSS polling for all").
                                bean("RSS2XMLBuilder", "runRssPollingForAllSubscribers");
                    }
                }
        );
        main.run(args);
    }

}

