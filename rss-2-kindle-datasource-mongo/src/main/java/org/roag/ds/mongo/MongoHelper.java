package org.roag.ds.mongo;

import com.mongodb.*;
import org.apache.camel.ProducerTemplate;
import org.roag.service.SubscriberFactory;
import org.roag.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by eurohlam on 05.10.16.
 */
public class MongoHelper
{
    final private static Logger logger = LoggerFactory.getLogger(MongoHelper.class);

    public static enum MONGO_OPERATION
    {
        FIND_ALL("findAll"),
        FIND_ONE("findOneByQuery"),
        INSERT("insert"),
        REMOVE("remove"),
        COUNT("count");

        private final String name;

        MONGO_OPERATION(String name)
        {
            this.name = name;
        }

        @Override
        public String toString()
        {
            return name;
        }
    }

    private String MONGO_SPRING_BEAN;

//    private String MONGO_CAMEL_ROUTE;

    private String defaultMongoDatabase;

    private String defaulMongoCollection;

    private SubscriberFactory subscriberFactory;

    private MongoHelper()
    {
    }

    public MongoHelper(String mongoBean)
    {
        this(mongoBean,null, null);
    }

    public MongoHelper(String mongoBean, String mongoDatabase, String mongoCollection)
    {
        this.MONGO_SPRING_BEAN = mongoBean;
//        this.MONGO_CAMEL_ROUTE = mongoCamelRoute;
        this.defaultMongoDatabase = mongoDatabase;
        this.defaulMongoCollection = mongoCollection;
        this.subscriberFactory = new SubscriberFactory();
    }

    public String getDefaultMongoDatabase()
    {
        return defaultMongoDatabase;
    }

    public String getDefaulMongoCollection()
    {
        return defaulMongoCollection;
    }

    DBObject findOneByCondition(ProducerTemplate producerTemplate, Map<String, String> conditions) throws Exception
    {
        return findOneByCondition(defaultMongoDatabase, defaulMongoCollection, producerTemplate, conditions);
    }

    DBObject findOneByCondition(String database, String collection, ProducerTemplate producerTemplate, Map<String, String> conditions) throws Exception
    {
        List<DBObject> r = findByCondition(database, collection, MONGO_OPERATION.FIND_ONE, producerTemplate, conditions);
        return r == null ? null : r.get(0);
    }


    List<DBObject> findAllByCondition(ProducerTemplate producerTemplate, Map<String, String> conditions) throws Exception
    {
        return findAllByCondition(defaultMongoDatabase, defaulMongoCollection, producerTemplate, conditions);
    }

    List<DBObject> findAllByCondition(String database, String collection, ProducerTemplate producerTemplate, Map<String, String> conditions) throws Exception
    {
        return findByCondition(database, collection, MONGO_OPERATION.FIND_ALL, producerTemplate, conditions);
    }

    private List<DBObject> findByCondition(String mongoDatabase, String collection, MONGO_OPERATION operation,
                                          ProducerTemplate producerTemplate, Map<String, String> conditions)
            throws Exception
    {
        List<DBObject> list=null;
        DBObject query = BasicDBObjectBuilder.start(conditions).get();
        Object result = producerTemplate.requestBody(getQuery(mongoDatabase, collection, operation), query);
        if (result == null)
        {
            logger.warn("Nothing found in Mongo for " + conditions);
            return null;
//            throw new Exception("Nothing found in Mongo for " + conditions);
        }

        if (result instanceof DBObject)
        {
            list = new ArrayList<>(1);
            list.add((DBObject) result);
        }
        else
        {
            list = (List) result;
        }

        if (logger.isDebugEnabled())
        {
            logger.debug("findByCondition {} returned the following results:", operation);
            for (DBObject obj : list)
            {
                for (String key : obj.keySet())
                    logger.debug("key = " + key + "   value = " + obj.get(key));
            }
        }
        return list;
    }

    String getQuery(String mongoDatabase, String collection, MONGO_OPERATION operation)
    {
        return "mongodb:" + MONGO_SPRING_BEAN + "?database=" + mongoDatabase + "&collection=" + collection + "&operation=" + operation.toString();
    }

    public List<Subscriber> getSubscribers(ProducerTemplate producerTemplate, Map<String, String> conditions) throws Exception
    {
//        DBObject query = BasicDBObjectBuilder.start("status", "active").get();
//        List<DBObject> result = producerTemplate.requestBody(MONGO_CAMEL_ROUTE, query, List.class);
        List<DBObject> result = findAllByCondition(producerTemplate, conditions);
        List<Subscriber> subscribers = new ArrayList<>(result.size());
        for (DBObject obj : result)
        {
            Subscriber subscr = subscriberFactory.convertJson2Pojo(Subscriber.class, subscriberFactory.convertPojo2Json(obj));
            subscribers.add(subscr);
            BasicDBList rsslist = (BasicDBList) obj.get("rsslist");
            logger.info("Subscriber: " + subscr.getEmail() + "\n" + rsslist.getClass().toString() + "  " + rsslist);
        }
        return subscribers;
    }

    public Subscriber getSubscriber(String email, ProducerTemplate producerTemplate) throws Exception
    {
        Map<String, String> cond = new HashMap<>(2);
        cond.put("status", "active");
        cond.put("email", email);
        DBObject result = findOneByCondition(producerTemplate, cond);
        if (result == null)
            throw new IllegalArgumentException("Subscriber " + email + " has not been found");

        result.removeField("_id");
        Subscriber subscr = subscriberFactory.convertJson2Pojo(Subscriber.class, subscriberFactory.convertPojo2Json(result));
        BasicDBList rsslist = (BasicDBList) result.get("rsslist");
        logger.info("Subscriber: {} \n {} {}", subscr.getEmail(), rsslist.getClass().toString(), rsslist);
        return subscr;

    }

    public WriteResult addSubscriber(Subscriber subscriber, ProducerTemplate producerTemplate) throws Exception
    {
        Map<String, String> cond= new HashMap<>(1);
        cond.put("email", subscriber.getEmail());
        DBObject r=findOneByCondition(producerTemplate,cond);
        if (r != null)
            throw new IllegalArgumentException("Subscriber " + subscriber.getEmail() + " already exists");

        WriteResult result = (WriteResult) producerTemplate.requestBody(
                getQuery(getDefaultMongoDatabase(), getDefaulMongoCollection(), MONGO_OPERATION.INSERT),
                subscriberFactory.convertPojo2Json(subscriber));

        logger.info("New subscriber: {} has been inserted into Mongo with the result: {}", subscriber.getEmail(), result);
        return result;
    }

    public WriteResult removeSubscriber(String email, ProducerTemplate producerTemplate) throws Exception
    {
        DBObject query = new BasicDBObject("email", email);
        WriteResult result = (WriteResult) producerTemplate.requestBody(
                getQuery(getDefaultMongoDatabase(), getDefaulMongoCollection(), MONGO_OPERATION.REMOVE),
                query);

        logger.warn("Subscriber: {} has been removed from Mongo with the result: {}", email, result);
        return result;
    }

}
