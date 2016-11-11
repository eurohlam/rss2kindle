package org.roag.mongo;

import com.google.gson.Gson;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by eurohlam on 05.10.16.
 */
public class MongoHelper
{
    final public static Logger logger = LoggerFactory.getLogger(MongoHelper.class);

    public static enum MONGO_OPERATION
    {
        FIND_ALL("findAll"), FIND_ONE("findOneByQuery");

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

    private String MONGO_CAMEL_ROUTE;

    private String defaultMongoDatabase;

    private String defaulMongoCollection;

    private SubscriberFactory subscriberFactory;

    private MongoHelper()
    {
    }

    public MongoHelper(String mongoBean, String mongoCamelRoute)
    {
        this(mongoBean, mongoCamelRoute, null, null);
    }

    public MongoHelper(String mongoBean, String mongoCamelRoute, String mongoDatabase, String mongoCollection)
    {
        this.MONGO_SPRING_BEAN = mongoBean;
        this.MONGO_CAMEL_ROUTE = mongoCamelRoute;
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

    public DBObject findOneByCondition(ProducerTemplate producerTemplate, Map<String, String> conditions)
    {
        return findOneByCondition(defaultMongoDatabase, defaulMongoCollection, producerTemplate, conditions);
    }

    public DBObject findOneByCondition(String database, String collection, ProducerTemplate producerTemplate, Map<String, String> conditions)
    {

        return findByCondition(database, collection, MONGO_OPERATION.FIND_ONE, producerTemplate, conditions).get(0);
    }


    public List<DBObject> findAllByCondition(ProducerTemplate producerTemplate, Map<String, String> conditions)
    {
        return findAllByCondition(defaultMongoDatabase, defaulMongoCollection, producerTemplate, conditions);
    }

    public List<DBObject> findAllByCondition(String database, String collection, ProducerTemplate producerTemplate, Map<String, String> conditions)
    {
        return findByCondition(database, collection, MONGO_OPERATION.FIND_ALL, producerTemplate, conditions);
    }

    public List<DBObject> findByCondition(String mongoDatabase, String collection, MONGO_OPERATION operation, ProducerTemplate producerTemplate, Map<String, String> conditions)
    {
        List<DBObject> list;
        DBObject query = BasicDBObjectBuilder.start(conditions).get();
        Object result = producerTemplate.requestBody(getQuery(mongoDatabase, collection, operation), query);
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

    public String getQuery(String mongoDatabase, String collection, MONGO_OPERATION operation)
    {
        return "mongodb:" + MONGO_SPRING_BEAN + "?database=" + mongoDatabase + "&collection=" + collection + "&operation=" + operation.toString();
    }

    public List<Subscriber> getSubscribers(ProducerTemplate producerTemplate) throws Exception
    {
        DBObject query = BasicDBObjectBuilder.start("status", "active").get();
        List<DBObject> result = producerTemplate.requestBody(MONGO_CAMEL_ROUTE, query, List.class);

        List<Subscriber> subscribers = new ArrayList<>(result.size());
        for (DBObject obj : result)
        {
            Subscriber subscr = subscriberFactory.getSubscriberFromDBObject(obj);
            subscribers.add(subscr);
            BasicDBList rsslist = (BasicDBList) obj.get("rsslist");
            logger.info("Subscriber: " + subscr.getEmail() + "\n" + rsslist.getClass().toString() + "  " + rsslist);
        }
        return subscribers;
    }

    public Subscriber getSubscriber(String email, ProducerTemplate producerTemplate)
    {
        Map<String, String> cond = new HashMap<>(2);
        cond.put("status", "active");
        cond.put("email", email);
        DBObject result = findOneByCondition(producerTemplate, cond);
        Subscriber subscr = subscriberFactory.getSubscriberFromDBObject(result);
        BasicDBList rsslist = (BasicDBList) result.get("rsslist");
        logger.info("Subscriber: {} \n {} {}", subscr.getEmail(), rsslist.getClass().toString(), rsslist);
        return subscr;

    }
}
