package org.roag.camel;

import org.apache.camel.Exchange;
import org.apache.camel.PropertyInject;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.testng.CamelSpringTestSupport;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.verify.VerificationTimes;
import org.roag.ds.SubscriberRepository;
import org.roag.ds.UserRepository;
import org.roag.model.Subscriber;
import org.roag.model.User;
import org.roag.service.ModelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.testng.asserts.Assertion;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * Created by eurohlam on 12.12.16.
 */
public class CamelRoutesTest extends CamelSpringTestSupport {

    private final Logger logger = LoggerFactory.getLogger(CamelRoutesTest.class);

    private UserRepository userRepository;
    private SubscriberRepository subscriberRepository;
    private User testUser;
    private Subscriber testSubscriber;
    private ModelFactory modelFactory = new ModelFactory();
    private ClientAndServer mockServer;

    private int httpPort = 1080;

    @PropertyInject("storage.path.rss")
    private String storagePathRss;

    @PropertyInject("rss.splitEntries")
    private String splitEntries;

    @PropertyInject("rss.feedHeader")
    private String feedHeader;

    @PropertyInject("rss.consumer.delay")
    private String consumerDelay;


    @BeforeTest
    public void startMockServer() {
        mockServer = ClientAndServer.startClientAndServer(httpPort);
    }

    @AfterTest
    public void stopMockServer() {
        mockServer.stop();
    }

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("META-INF/spring/test-spring-context.xml");
        userRepository = (UserRepository) context.getBean("userRepository");
        subscriberRepository = (SubscriberRepository) context.getBean("subscriberRepository");
        testUser = (User) context.getBean("testUser");
        testSubscriber = (Subscriber) context.getBean("testSubscriber");
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

    @Test
    public void testMockHttpServer() throws Exception {
        mockServer
                .when(HttpRequest.request().withMethod("GET").withPath("/feed"))
                .respond(HttpResponse.response().withStatusCode(200).withBody(getFeedContent()));
        template.sendBody("direct:http", null);
        getMockEndpoint("mock:result").expectedMessageCount(1);
    }

    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() throws Exception {
                from("direct:bean").to("bean:rss2XmlHandler?method=runRssPollingForAllUsers");

                from("direct:http")
                        .setHeader(Exchange.HTTP_METHOD, constant(org.apache.camel.component.http4.HttpMethods.GET))
                        .to("http4://localhost:" + httpPort + "/feed")
                        .to("mock:result");
            }
        };
    }

    private String getFeedContent() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<rss version=\"2.0\"" +
                " xmlns:content=\"http://purl.org/rss/1.0/modules/content/\"" +
                " xmlns:wfw=\"http://wellformedweb.org/CommentAPI/\"" +
                " xmlns:dc=\"http://purl.org/dc/elements/1.1/\"" +
                " xmlns:atom=\"http://www.w3.org/2005/Atom\"" +
                " xmlns:sy=\"http://purl.org/rss/1.0/modules/syndication/\"" +
                " xmlns:slash=\"http://purl.org/rss/1.0/modules/slash/\"" +
                ">" +
                "   <channel>" +
                "       <title>RSS2KINDLE BLOG</title>" +
                "       <link>http://localhost</link>" +
                "       <description>Something cool</description>" +
                "       <lastBuildDate>Mon, 15 May 2019 09:29:46 +0000</lastBuildDate>" +
                "       <language>ru-RU</language>" +
                "       <sy:updatePeriod>hourly</sy:updatePeriod>" +
                "       <sy:updateFrequency>1</sy:updateFrequency>" +
                "       <generator>https://wordpress.org/?v=4.8</generator>" +
                "       <item>" +
                "           <title>Story 1</title>" +
                "           <link>http://localhost/story-1/</link>" +
                "           <comments>http://localhost/#respond</comments>" +
                "           <pubDate>Mon, 15 May 2019 13:29:46 +0000</pubDate>" +
                "           <dc:creator><![CDATA[eurohlam]]></dc:creator>" +
                "           <category><![CDATA[test]]></category>" +
                "           <guid isPermaLink=\"false\">http://localhost/?p=1</guid>" +
                "           <description><![CDATA[something new]]></description>" +
                "           <content:encoded><![CDATA[This is new story]]></content:encoded>" +
                "       </item>" +
                "       <item>" +
                "           <title>Story 2</title>" +
                "           <link>http://localhost/story-2/</link>" +
                "           <comments>http://localhost/#respond</comments>" +
                "           <pubDate>Mon, 9 Jul 2018 13:29:46 +0000</pubDate>" +
                "           <dc:creator><![CDATA[eurohlam]]></dc:creator>" +
                "           <category><![CDATA[test]]></category>" +
                "           <guid isPermaLink=\"false\">http://localhost/?p=1</guid>" +
                "           <description><![CDATA[something old]]></description>" +
                "           <content:encoded><![CDATA[This is old story]]></content:encoded>" +
                "       </item>" +
                "   </channel>" +
                "</rss>";
    }

    @Test(groups = {"CamelTesting"})
    public void runPollingTest() throws Exception {
        mockServer
                .when(HttpRequest.request().withMethod("GET").withPath("/1/feed"))
                .respond(HttpResponse.response().withStatusCode(200).withBody(getFeedContent()));
        mockServer
                .when(HttpRequest.request().withMethod("GET").withPath("/2/feed"))
                .respond(HttpResponse.response().withStatusCode(200).withBody(getFeedContent()));
        mockServer
                .when(HttpRequest.request().withMethod("GET").withPath("/3/feed"))
                .respond(HttpResponse.response().withStatusCode(200).withBody(getFeedContent()));

        //updating testuser that is defined in spring config
        userRepository.addUser(testUser);
        subscriberRepository.addSubscriber(testUser.getUsername(), testSubscriber);
        subscriberRepository.addSubscriber(testUser.getUsername(), modelFactory.newSubscriber("test2@test.com", "test2", "http://localhost:" + httpPort + "/2/feed"));
        subscriberRepository.addSubscriber(testUser.getUsername(), modelFactory.newSubscriber("test3@test.com", "test3", "http://localhost:" + httpPort + "/3/feed"));
        //creating a brand new user
        User newUser = modelFactory.newUser("newUser", "newuser@test.com", "newUser");
        userRepository.addUser(newUser);
        subscriberRepository.addSubscriber(newUser.getUsername(),
                modelFactory.newSubscriber(
                        "newtest@test.com",
                        "newtest",
                        new String[]{"http://localhost:" + httpPort + "/1/feed", "http://localhost:" + httpPort + "/2/feed"},
                        LocalDateTime.now(),
                        24,
                        TimeUnit.HOURS));

        logger.info("User state before polling: {}", modelFactory.pojo2Json(userRepository.getUser(testUser.getUsername())));
        template.sendBody("direct:bean", null);

        Assert.assertEquals(testUser.getSubscribers().get(0).getRsslist().get(0).getStatus(), "active", "Status of rss is not active");
        Assert.assertEquals(newUser.getSubscribers().get(0).getRsslist().get(0).getStatus(), "active", "Status of rss is not active");
    }
}
