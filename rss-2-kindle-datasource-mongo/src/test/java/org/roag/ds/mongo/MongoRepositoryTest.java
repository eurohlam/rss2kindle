package org.roag.ds.mongo;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.testng.CamelSpringTestSupport;
import org.roag.ds.Injector;
import org.roag.ds.OperationResult;
import org.roag.service.SubscriberFactory;
import org.roag.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
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
public class MongoRepositoryTest extends CamelSpringTestSupport
{
    final private static Logger logger = LoggerFactory.getLogger(MongoRepositoryTest.class);

    @EndpointInject(uri = "mock:result")
    protected MockEndpoint resultEndpoint;

    private MongoHelper mh;
    private MongoSubscriberRepository mongoRepository;

    private SubscriberFactory subscriberFactory = new SubscriberFactory();

    private final String TEST_EMAIL="test@gmail.com";

    /**
     * We need this override to avoid reloading of context for each test method
     *
     * @return
     */
    @Override
    public boolean isCreateCamelContextPerClass()
    {
        return true;
    }


    @Override
    protected AbstractApplicationContext createApplicationContext()
    {
        ClassPathXmlApplicationContext context=new ClassPathXmlApplicationContext("META-INF/spring/mongo-test-context.xml");
        mh = context.getBean(MongoHelper.class);
        mongoRepository = context.getBean(MongoSubscriberRepository.class);
        return context;
    }

    @Test(groups = {"Functionality Check"})
    public void getQueryTest()
    {
        String q = mh.getQuery(mh.getDefaultMongoDatabase(), mh.getDefaulMongoCollection(), MongoHelper.MONGO_OPERATION.FIND_ONE);
        logger.info("MongoTest: findOneByQuery - " + q);
        assertNotNull(q, "MongoHelper returns empty findOneByQuery");
        assertStringContains(q, "operation=findOneByQuery");
    }

    @Test(groups = {"Connectivity Check"})
    @BeforeGroups(groups = {"Mongo Integration"})
    public void runConnectivityTest() throws Exception
    {
        Long result = template.requestBody("direct:count", null, Long.class);
        logger.debug("Connectivity test result: Count = {}", result);
        resultEndpoint.assertIsSatisfied();
        assertTrue(result instanceof Long, "Result is not of type Long");
    }

    @Test(groups = {"Mongo Integration"})
    public void mongoCRUDTest() throws Exception
    {
        Subscriber s = subscriberFactory.newSubscriber(TEST_EMAIL, "test", "test.org/feed");
        OperationResult r=mongoRepository.addSubscriber(s);

        assertEquals(r, OperationResult.SUCCESS);

        Subscriber ns=mongoRepository.getSubscriber(TEST_EMAIL);
        assertEquals(ns.getName(), s.getName());

        assertTrue(mongoRepository.findAll().size()>0);

        OperationResult d = mongoRepository.removeSubscriber(TEST_EMAIL);
        assertEquals(d, OperationResult.SUCCESS);
    }

    @Test(groups = {"Mongo Integration"})
    public void mongoHelperAddSubscriberTest() throws Exception
    {
        Subscriber s = subscriberFactory.newSubscriber(TEST_EMAIL, "test", "test.org/feed");
        WriteResult r= mh.addSubscriber(s, template);

        assertTrue(!r.isUpdateOfExisting());
    }


    @Test(groups = {"Mongo Integration"}, dependsOnMethods = {"mongoHelperAddSubscriberTest"})
    public void mongoHelperFindAllTest() throws Exception
    {
        HashMap<String, String> cond = new HashMap<>(1);
        cond.put("status", "active");
        List<DBObject> result = mh.findAllByCondition(template, cond);

        assertTrue(result instanceof List);

        for (DBObject obj : result)
        {
            logger.debug(obj.get("_id").toString());
            logger.debug(obj.get("email").toString());
            logger.debug(obj.get("name").toString());
            logger.debug(obj.get("status").toString());
            assertNotNull(obj.get("_id").toString(), "DBObject in returned list should contain field _id");
            assertNotNull(obj.get("email").toString(), "DBObject in returned list should contain field email");
            assertNotNull(obj.get("name").toString(), "DBObject in returned list should contain field name");
        }
    }

    @Test(groups = {"Mongo Integration"}, dependsOnMethods = {"mongoHelperAddSubscriberTest"})
    public void mongoHelperFindOneTest() throws Exception
    {
        HashMap<String, String> cond = new HashMap<>(1);
        cond.put("email", TEST_EMAIL);
        DBObject result = mh.findOneByCondition(template, cond);

        assertTrue(result instanceof DBObject);

        logger.debug(result.get("_id").toString());
        logger.debug(result.get("email").toString());
        logger.debug(result.get("name").toString());
        logger.debug(result.get("status").toString());
        assertNotNull(result.get("_id").toString(), "DBObject in returned list should contain field _id");
        assertNotNull(result.get("email").toString(), "DBObject in returned list should contain field email");
        assertNotNull(result.get("name").toString(), "DBObject in returned list should contain field name");
    }



    @Test(groups = {"Mongo Cleansing"}, dependsOnMethods = {"mongoHelperAddSubscriberTest"})
    @AfterGroups(dependsOnGroups = {"Mongo Integration", "Functionality Check"})
    public void mongoHelperRemoveSubscriberTest() throws Exception
    {
        WriteResult r= mh.removeSubscriber(TEST_EMAIL, template);

        assertTrue(r.getN()>0);
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception
    {
        return new RouteBuilder()
        {
            @Override
            public void configure() throws Exception
            {
                from("direct:count").id("Test Mongo Count").
                        to(mh.getQuery(mh.getDefaultMongoDatabase(), mh.getDefaulMongoCollection(), MongoHelper.MONGO_OPERATION.COUNT)).
                        to("mock:result");

                from("direct:findOneTest").id("Test Mongo findOne").
                        to(mh.getQuery(mh.getDefaultMongoDatabase(), mh.getDefaulMongoCollection(), MongoHelper.MONGO_OPERATION.FIND_ONE)).
                        to("mock:result");

                from("direct:findAllTest").id("Test Mongo findAllQuery").
                        to(mh.getQuery(mh.getDefaultMongoDatabase(), mh.getDefaulMongoCollection(), MongoHelper.MONGO_OPERATION.FIND_ALL)).
                        to("mock:result");
            }
        };
    }
}
