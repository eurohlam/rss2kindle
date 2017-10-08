package org.roag.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.PropertyInject;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.testng.CamelSpringTestSupport;
import org.roag.ds.SubscriberRepository;
import org.roag.model.Subscriber;
import org.roag.model.User;
import org.roag.service.SubscriberFactory;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.Test;

/**
 * Created by eurohlam on 12.12.16.
 */
public class CamelRoutesTest extends CamelSpringTestSupport
{
    private SubscriberRepository subscriberRepository;
    private User testUser;
    private Subscriber testSubscriber;
    private Rss2XmlHandler builder;
    private SubscriberFactory subscriberFactory = new SubscriberFactory();

    @PropertyInject("storage.path.rss")
    private String storagePathRss;


    @Override
    protected AbstractApplicationContext createApplicationContext()
    {
        ClassPathXmlApplicationContext context=new ClassPathXmlApplicationContext("META-INF/spring/test-spring-context.xml");
        subscriberRepository = (SubscriberRepository) context.getBean("subscriberRepository");
        testUser = (User) context.getBean("testUser");
        testSubscriber = (Subscriber) context.getBean("testSubscriber");
        builder = context.getBean(Rss2XmlHandler.class);
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
        subscriberRepository.addUser(testUser);
        subscriberRepository.addSubscriber(testUser.getUsername(), testSubscriber);
        subscriberRepository.addSubscriber(testUser.getUsername(), subscriberFactory.newSubscriber("test2@test.com", "test2", "file:src/test/resources/testrss.xml"));
        builder.runRssPollingForAllUsers();
        //wait for polling before stopping of context
        Thread.sleep(15000);
    }

}
