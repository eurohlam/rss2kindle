package org.roag.camel;

import org.apache.camel.Exchange;
import org.apache.camel.PropertyInject;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.testng.CamelSpringTestSupport;
import org.roag.ds.SubscriberRepository;
import org.roag.ds.UserRepository;
import org.roag.model.Subscriber;
import org.roag.model.User;
import org.roag.service.ModelFactory;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.Test;

/**
 * Created by eurohlam on 12.12.16.
 */
public class CamelRoutesTest extends CamelSpringTestSupport {
    private UserRepository userRepository;
    private SubscriberRepository subscriberRepository;
    private User testUser;
    private Subscriber testSubscriber;
    private Rss2XmlHandler builder;
    private ModelFactory modelFactory = new ModelFactory();

    @PropertyInject("storage.path.rss")
    private String storagePathRss;

    @PropertyInject("rss.splitEntries")
    private String splitEntries;
    @PropertyInject("rss.feedHeader")
    private String feedHeader;
    @PropertyInject("rss.consumer.delay")
    private String consumerDelay;

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("META-INF/spring/test-spring-context.xml");
        userRepository = (UserRepository) context.getBean("userRepository");
        subscriberRepository = (SubscriberRepository) context.getBean("subscriberRepository");
        testUser = (User) context.getBean("testUser");
        testSubscriber = (Subscriber) context.getBean("testSubscriber");
        builder = context.getBean(Rss2XmlHandler.class);
        return context;
    }

    @Override
    public boolean isCreateCamelContextPerClass() {
        return true;
    }

    @Override
    public boolean isUseAdviceWith() {
        return false;
    }

    public void startCamelContext() throws Exception {
        context.start();
    }


    public void stopCamelContext() throws Exception {
        context.stop();
    }

    @Test(groups = {"CamelTesting"})
    public void runPollingTest() throws Exception {
        userRepository.addUser(testUser);
        subscriberRepository.addSubscriber(testUser.getUsername(), testSubscriber);
        subscriberRepository.addSubscriber(testUser.getUsername(), modelFactory.newSubscriber("test2@test.com", "test2", "file:src/test/resources/testrss.xml"));
        builder.runRssPollingForAllUsers();
        //wait for polling before stopping of context
        Thread.sleep(25000);
    }


    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() throws Exception {
                from("direct:rss").from("rss:file:src/test/resources/testrss.xml?splitEntries=" + splitEntries + "&sortEntries=true&consumer.delay=" + consumerDelay + "&feedHeader=" + feedHeader).
                        marshal().rss().to("mock:result");
                from("direct:http")
                        .setHeader(Exchange.HTTP_METHOD, constant(org.apache.camel.component.http4.HttpMethods.GET))
                        .to("http4://google.com")
                        .to("file://test/data/output.xml");
            }
        };
    }

    @Test
    public void testListOfEntriesIsSplitIntoPieces() throws Exception {

        template.sendBody("direct:http", null);
/*
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(1);
        mock.assertIsSatisfied();
*/

//        template.requestBody("direct:http", new Object());
    }

}
