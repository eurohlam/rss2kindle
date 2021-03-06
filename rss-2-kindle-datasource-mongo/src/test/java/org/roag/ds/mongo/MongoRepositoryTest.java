package org.roag.ds.mongo;

import com.mongodb.DBObject;
import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.component.mongodb.MongoDbOperation;
import org.apache.camel.testng.CamelSpringTestSupport;
import org.roag.ds.OperationResult;
import org.roag.service.ModelFactory;
import org.roag.model.*;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.AbstractApplicationContext;

import java.util.HashMap;
import java.util.List;

/**
 * Created by eurohlam on 06.10.16.
 * Test for checking interaction with MongoDB via Camel
 */
public class MongoRepositoryTest extends CamelSpringTestSupport {

    private final Logger logger = LoggerFactory.getLogger(MongoRepositoryTest.class);

    @EndpointInject(uri = "mock:result")
    private MockEndpoint resultEndpoint;

    private MongoHelper mh;
    private MongoUserRepository userRepository;
    private MongoSubscriberRepository subscriberRepository;

    private ModelFactory modelFactory = new ModelFactory();

    private static final String TEST_EMAIL = "test@gmail.com";
    private static final String TEST_USERNAME = "testUser";

    /**
     * We need this override to avoid reloading of context for each test method
     *
     * @return true
     */
    @Override
    public boolean isCreateCamelContextPerClass() {
        return true;
    }


    @Override
    protected AbstractApplicationContext createApplicationContext() {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("META-INF/spring/mongo-test-context.xml");
        mh = context.getBean(MongoHelper.class);
        userRepository = context.getBean(MongoUserRepository.class);
        subscriberRepository = context.getBean(MongoSubscriberRepository.class);
        return context;
    }

    @Test(groups = {"Functionality Check"})
    public void getQueryTest() {
        String q = mh.getQuery(mh.getDefaultMongoDatabase(), mh.getDefaulMongoCollection(), MongoDbOperation.findOneByQuery);
        logger.info("MongoTest: findOneByQuery - " + q);
        assertNotNull(q, "MongoHelper returns empty findOneByQuery");
        assertStringContains(q, "operation=findOneByQuery");
    }

    @Test(groups = {"Connectivity Check"}, dependsOnGroups = {"Functionality Check"})
    public void runConnectivityTest() throws Exception {
        Long result = template.requestBody("direct:count", null, Long.class);
        logger.debug("Connectivity test result: Count = {}", result);
        resultEndpoint.assertIsSatisfied();
        assertTrue(result instanceof Long, "Result is not of type Long");
    }

    @Test(groups = {"Mongo repository CRUD"}, dependsOnGroups = {"Mongo Cleansing", "Connectivity Check"})
    public void mongoUserCrudTest() throws Exception {
        User user = modelFactory.newUser(TEST_USERNAME, TEST_EMAIL, "123");
        //create user
        OperationResult result = userRepository.addUser(user);
        assertEquals(result, OperationResult.SUCCESS, "Could not create a user");
        //lock user
        result = userRepository.lockUser(TEST_USERNAME);
        assertEquals(result, OperationResult.SUCCESS, "Could not lock a user");
        //read locked user
        user = userRepository.getUser(TEST_USERNAME);
        assertEquals(user.getStatus(), UserStatus.LOCKED.toString(), "User status should be LOCKED, but it is not");
        //unlock user
        result = userRepository.unlockUser(TEST_USERNAME);
        assertEquals(result, OperationResult.SUCCESS, "Could not unlock a user");
        //read unlocked user
        user = userRepository.getUser(TEST_USERNAME);
        assertEquals(user.getStatus(), UserStatus.ACTIVE.toString(), "User status should be ACTIVE, but it is not");
        //remove user
        result = userRepository.removeUser(TEST_USERNAME);
        assertEquals(result, OperationResult.SUCCESS, "Could not remove a user");
    }


    @Test(groups = {"Mongo repository CRUD"}, dependsOnGroups = {"Mongo Cleansing", "Connectivity Check"})
    public void mongoSubscriberCrudTest() throws Exception {
        User user = modelFactory.newUser(TEST_USERNAME, TEST_EMAIL, "123");
        //create user
        OperationResult result = userRepository.addUser(user);
        assertEquals(result, OperationResult.SUCCESS);

        Subscriber subscriber = modelFactory.newSubscriber(TEST_EMAIL, "test", "test.org/feed");
        //create subscriber
        result = subscriberRepository.addSubscriber(TEST_USERNAME, subscriber);
        assertEquals(result, OperationResult.SUCCESS);
        //read subscriber
        Subscriber newSubscriber = subscriberRepository.getSubscriber(TEST_USERNAME, TEST_EMAIL);
        assertEquals(newSubscriber.getName(), subscriber.getName());
        //read all users
        assertTrue(userRepository.findAll().size() > 0);
        //update subscriber
        newSubscriber.setName("new_test_name");
        result = subscriberRepository.updateSubscriber(TEST_USERNAME, newSubscriber);
        assertEquals(result, OperationResult.SUCCESS);
        //read updated subscriber
        newSubscriber = subscriberRepository.getSubscriber(TEST_USERNAME, TEST_EMAIL);
        assertEquals(newSubscriber.getName(), "new_test_name");
        //suspend (update) subscriber
        result = subscriberRepository.suspendSubscriber(TEST_USERNAME, newSubscriber.getEmail());
        assertEquals(result, OperationResult.SUCCESS);
        //read suspended subscriber
        newSubscriber = subscriberRepository.getSubscriber(TEST_USERNAME, TEST_EMAIL);
        assertEquals(newSubscriber.getStatus(), SubscriberStatus.SUSPENDED.toString());
        //resume (update) subscriber
        result = subscriberRepository.resumeSubscriber(TEST_USERNAME, newSubscriber.getEmail());
        assertEquals(result, OperationResult.SUCCESS);
        //read resumed subscriber
        newSubscriber = subscriberRepository.getSubscriber(TEST_USERNAME, TEST_EMAIL);
        assertEquals(newSubscriber.getStatus(), SubscriberStatus.ACTIVE.toString());
        //delete subscriber
        OperationResult d = subscriberRepository.removeSubscriber(TEST_USERNAME, TEST_EMAIL);
        assertEquals(d, OperationResult.SUCCESS);
        //remove user
        result = userRepository.removeUser(TEST_USERNAME);
        assertEquals(result, OperationResult.SUCCESS);
    }

    @Test(groups = {"Mongo Integration"}, dependsOnGroups = {"Connectivity Check"})
    public void mongoHelperAddUserTest() throws Exception {
        User user = modelFactory.newUser(TEST_USERNAME, TEST_EMAIL, "123");
        Subscriber s = modelFactory.newSubscriber(TEST_EMAIL, "test", "test.org/feed");
        user.getSubscribers().add(s);
        OperationResult r = mh.addUser(user, template);

        assertTrue(r == OperationResult.SUCCESS);
    }

    @Test(groups = {"Mongo Integration"}, dependsOnMethods = {"mongoHelperAddUserTest"})
    public void mongoHelperUpdateUserTest() throws Exception {
        User user = mh.getUser(TEST_USERNAME, template);
        Subscriber s = modelFactory.newSubscriber(TEST_EMAIL, "updated_test", "updated_test.org/feed");
        user.getSubscribers().add(s);
        OperationResult r = mh.updateUser(user, template);

        assertTrue(r == OperationResult.SUCCESS);
    }

    @Test(groups = {"Mongo Integration"}, dependsOnMethods = {"mongoHelperAddUserTest"})
    public void mongoHelperFindAllTest() throws Exception {
        HashMap<String, String> cond = new HashMap<>(1);
        cond.put("status", UserStatus.ACTIVE.toString());
        List<DBObject> result = mh.findAllByCondition(template, cond);

        assertTrue(result instanceof List);

        for (DBObject obj : result) {
            logger.debug(obj.get("_id").toString());
            logger.debug(obj.get("password").toString());
            logger.debug(obj.get("username").toString());
            logger.debug(obj.get("status").toString());
            assertNotNull(obj.get("_id").toString(), "DBObject in returned list should contain field _id");
            assertNotNull(obj.get("password").toString(), "DBObject in returned list should contain field password");
            assertNotNull(obj.get("username").toString(), "DBObject in returned list should contain field username");
        }
    }

    @Test(groups = {"Mongo Integration"}, dependsOnMethods = {"mongoHelperAddUserTest"})
    public void mongoHelperFindOneTest() throws Exception {
        HashMap<String, String> cond = new HashMap<>(1);
        cond.put("username", TEST_USERNAME);
        DBObject result = mh.findOneByCondition(template, cond);

        assertTrue(result instanceof DBObject);

        logger.debug(result.get("_id").toString());
        logger.debug(result.get("password").toString());
        logger.debug(result.get("username").toString());
        logger.debug(result.get("status").toString());
        assertNotNull(result.get("_id").toString(), "DBObject in returned list should contain field _id");
        assertNotNull(result.get("password").toString(), "DBObject in returned list should contain field password");
        assertNotNull(result.get("username").toString(), "DBObject in returned list should contain field username");
    }


    @Test(groups = {"Mongo Cleansing"}, dependsOnGroups = {"Mongo Integration"})
    public void mongoHelperRemoveUserTest() throws Exception {
        OperationResult r = mh.removeUser(TEST_USERNAME, template);

        assertTrue(r == OperationResult.SUCCESS);
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:count").id("Test Mongo Count")
                        .to(mh.getQuery(mh.getDefaultMongoDatabase(), mh.getDefaulMongoCollection(), MongoDbOperation.count))
                        .to("mock:result");

                from("direct:findOneTest").id("Test Mongo findOne")
                        .to(mh.getQuery(mh.getDefaultMongoDatabase(), mh.getDefaulMongoCollection(), MongoDbOperation.findOneByQuery))
                        .to("mock:result");

                from("direct:findAllTest").id("Test Mongo findAllQuery")
                        .to(mh.getQuery(mh.getDefaultMongoDatabase(), mh.getDefaulMongoCollection(), MongoDbOperation.findAll))
                        .to("mock:result");
            }
        };
    }
}
