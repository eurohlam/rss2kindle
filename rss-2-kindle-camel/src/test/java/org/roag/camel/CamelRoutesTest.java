package org.roag.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.PropertyInject;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.testng.CamelSpringTestSupport;
import org.roag.ds.SubscriberRepository;
import org.roag.model.Subscriber;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.Test;

/**
 * Created by eurohlam on 12.12.16.
 */
public class CamelRoutesTest extends CamelSpringTestSupport
{
    private SubscriberRepository subscriberRepository;
    private Subscriber testSubscriber;
    private RSS2XMLBuilder builder;

    @PropertyInject("storage.path.rss")
    private String storagePathRss;


    @Override
    protected AbstractApplicationContext createApplicationContext()
    {
        ClassPathXmlApplicationContext context=new ClassPathXmlApplicationContext("META-INF/spring/test-spring-context.xml");
        subscriberRepository = (SubscriberRepository) context.getBean("subscriberRepository");
        testSubscriber = (Subscriber) context.getBean("testSubscriber");
        builder = context.getBean(RSS2XMLBuilder.class);
        return context;
    }

    @Override
    public boolean isCreateCamelContextPerClass()
    {
        return true;
    }

    @Override
    public boolean isUseAdviceWith()
    {
        return false;
    }

    public void startCamelContext() throws Exception
    {
        context.start();
    }


    public void stopCamelContext() throws Exception
    {
        context.stop();
    }

    @Test(groups = {"CamelTesting"})
    public void runPollingTest() throws Exception
    {
        subscriberRepository.addSubscriber(testSubscriber);
        sub
        builder.runRssPollingForAllSubscribers();
        //wait for polling before stopping of context
        Thread.sleep(15000);
    }

//    @Test(groups = {"CamelTesting"})
    public void runRss()
    {
//        template.requestBody("direct:rss", new Object());
//        assertTrue(result instanceof Long, "Result is not of type Long");
    }

/*
    @Override
    protected RouteBuilder createRouteBuilder() throws Exception
    {
        return new RouteBuilder()
        {
            @Override
            public void configure() throws Exception
            {
               from("direct:rss").
                       from("rss:http://justtralala.com/feed?feedHeader=false&splitEntries=true").
                       marshal().rss().
                       to("file://" + storagePathRss);
            }
        };
    }
*/
}
