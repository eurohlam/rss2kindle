package org.roag.mongo;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;
import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.testng.CamelSpringTestSupport;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.*;

import org.roag.camel.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.AbstractApplicationContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by eurohlam on 06.10.16.
 */
public class MongoHelperTest extends CamelSpringTestSupport
{
    final public static Logger logger= LoggerFactory.getLogger(MongoHelperTest.class);

    @EndpointInject(uri = "mock:result")
    protected MockEndpoint resultEndpoint;

    protected MongoHelper mh= ServiceLocator.getBean(MongoHelper.class);

    /**
     * We need this override to avoid reloading of context for each test method
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
        return  (ClassPathXmlApplicationContext) ServiceLocator.getContext();
    }

    @Test(groups = {"checking"})
    public void dbParamsTest()
    {
        String db=mh.getMongoHost()+ ":" + mh.getMongoPort()+ ":" + mh.getMongoDatabase();
        logger.info(db);
        assertNotNull(mh.getMongoHost());
        assertNotNull(mh.getMongoPort());
        assertNotNull(mh.getMongoDatabase());
    }

    @Test(groups = {"checking"})
    public void findOneByQueryTest()
    {
        String q=mh.findOneByQuery();
        logger.info("MongoTest: findOneByQuery - " + q);
        assertNotNull(q, "MongoHelper returns empty findOneByQuery");
    }

    @Test(groups = {"integration"})
    public void convertDBObject2PojoTest() throws Exception
    {
        DBObject query = BasicDBObjectBuilder.start("email", "fbroman@mail.ru").get();
        DBObject result  = template.requestBody("direct:findOneTest", query, DBObject.class);

        logger.info(result.get("_id").toString());
        logger.info(result.get("email").toString());
        logger.info(result.get("name").toString());
        resultEndpoint.assertIsSatisfied();

        Subscriber subscr=mh.convertDBObject2Pojo(Subscriber.class, result);
        assertEquals(subscr.getEmail(), "fbroman@mail.ru", "GSON converter does not work");
    }

    @Test(groups = {"integration"})
    public void runFindOneByQueryTest() throws Exception
    {
        DBObject query = BasicDBObjectBuilder.start("email", "fbroman@mail.ru").get();
        DBObject result  = template.requestBody("direct:findOneTest", query, DBObject.class);

        logger.info(result.get("_id").toString());
        logger.info(result.get("email").toString());
        logger.info(result.get("name").toString());

        resultEndpoint.assertIsSatisfied();

        assertNotNull(result.get("_id").toString(), "DBObject in returned list should contain field _id");
        assertNotNull(result.get("email").toString(), "DBObject in returned list should contain field email");
        assertNotNull(result.get("name").toString(), "DBObject in returned list should contain field name");
    }

    @Test(groups = {"integration"})
    public void runFindAllTest() throws Exception
    {
        DBObject query = BasicDBObjectBuilder.start("status", "active").get();
        List<DBObject> result  = template.requestBody("direct:mongoFindAll", query, List.class);//mh.findAllByCondition(template,cond);

        assertTrue(result instanceof List);

        for (DBObject obj:result)
        {
            logger.info(obj.get("_id").toString());
            logger.info(obj.get("email").toString());
            logger.info(obj.get("name").toString());
            logger.info(obj.get("status").toString());
            assertNotNull(obj.get("_id").toString(), "DBObject in returned list should contain field _id");
            assertNotNull(obj.get("email").toString(), "DBObject in returned list should contain field email");
            assertNotNull(obj.get("name").toString(), "DBObject in returned list should contain field name");
        }

        resultEndpoint.assertIsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception
    {
        return new RouteBuilder(){
            @Override
            public void configure() throws Exception
            {
                from("direct:findOneTest").id("Test Mongo findOne").
                        to(mh.findOneByQuery()).
                        to("mock:result");

                from("direct:findAllTest").id("Test Mongo findAllQuery").
                        to(mh.findAllQuery()).
                        to("mock:result");
            }
        };
    }
}
