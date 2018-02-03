package org.roag.ds.mongo;

import com.mongodb.*;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mongodb.MongoDbConstants;
import org.apache.camel.component.mongodb.MongoDbOperation;
import org.roag.ds.OperationResult;
import org.roag.service.SubscriberFactory;
import org.roag.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by eurohlam on 05.10.16.
 */
public class MongoHelper
{
    final private static Logger logger = LoggerFactory.getLogger(MongoHelper.class);

    private static final ThreadLocal<SimpleDateFormat> dateFormat = new ThreadLocal<SimpleDateFormat>(){
        @Override
        protected SimpleDateFormat initialValue()
        {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };

    private String MONGO_SPRING_BEAN;

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
        List<DBObject> r = findByCondition(database, collection, MongoDbOperation.findOneByQuery, producerTemplate, conditions);
        return r == null ? null : r.get(0);
    }


    List<DBObject> findAllByCondition(ProducerTemplate producerTemplate, Map<String, String> conditions) throws Exception
    {
        return findAllByCondition(defaultMongoDatabase, defaulMongoCollection, producerTemplate, conditions);
    }

    List<DBObject> findAllByCondition(String database, String collection, ProducerTemplate producerTemplate, Map<String, String> conditions) throws Exception
    {
        return findByCondition(database, collection, MongoDbOperation.findAll, producerTemplate, conditions);
    }

    private List<DBObject> findByCondition(String mongoDatabase, String collection, MongoDbOperation operation,
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

    String getQuery(String mongoDatabase, String collection, MongoDbOperation operation)
    {
        return "mongodb:" + MONGO_SPRING_BEAN + "?database=" + mongoDatabase + "&collection=" + collection + "&operation=" + operation.toString();
    }

    public User getUser(String username, ProducerTemplate producerTemplate) throws Exception
    {
        Map<String, String> cond = new HashMap<>(2);
//        cond.put("status", "active");
        cond.put("username", username);
        DBObject result = findOneByCondition(producerTemplate, cond);
        if (result == null)
            throw new IllegalArgumentException("User " + username + " has not been found");

        result.removeField("_id");
        User user = subscriberFactory.convertJson2Pojo(User.class, subscriberFactory.convertPojo2Json(result));
        BasicDBList subscribers = (BasicDBList) result.get("subscribers");
        logger.info("GET: User: {} with status {} \n {} {}", user.getUsername(), user.getStatus(), subscribers.getClass().toString(), subscribers);
        return user;

    }

    public OperationResult addUser(User user, ProducerTemplate producerTemplate) throws Exception
    {
        Map<String, String> cond= new HashMap<>(1);
        cond.put("username", user.getUsername());
        DBObject r=findOneByCondition(producerTemplate,cond);
        if (r != null)
            throw new IllegalArgumentException("Creation is impossible. User " + user.getUsername() + " already exists");

        Object result = producerTemplate.requestBody(
                getQuery(getDefaultMongoDatabase(), getDefaulMongoCollection(), MongoDbOperation.insert),
                subscriberFactory.convertPojo2Json(user));

        logger.info("INSERT: New user: {} has been inserted into Mongo with the result: {}", user.getUsername(), result);
        logger.debug("INSERT: result: {}", result);
        return OperationResult.SUCCESS;
    }

    public OperationResult updateUser(User user, ProducerTemplate producerTemplate) throws Exception
    {
        user.setDateModified(dateFormat.get().format(new Date()));
        Map<String, String> cond= new HashMap<>(1);
        cond.put("username", user.getUsername());
        DBObject r=findOneByCondition(producerTemplate,cond);
        if (r == null)
            throw new IllegalArgumentException("Update is impossible. User " + user.getUsername() + " does not exist");

        DBObject filterField = new BasicDBObject("username", user.getUsername());
        DBObject obj=new BasicDBObject("$set", BasicDBObjectBuilder.start(subscriberFactory.convertJson2Pojo(Map.class,subscriberFactory.convertPojo2Json(user))).get());
        Object result = producerTemplate.requestBody(
                getQuery(getDefaultMongoDatabase(), getDefaulMongoCollection(), MongoDbOperation.update),
                new Object[]{filterField, obj});//TODO: process UpdateResult

        logger.info("UPDATE: User: {} has been updated into Mongo with the result: {}", user.getUsername(), result);
        logger.debug("UPDATE: result: {}", result);
        return OperationResult.SUCCESS;
    }

    public OperationResult removeUser(String username, ProducerTemplate producerTemplate) throws Exception
    {
        DBObject query = new BasicDBObject("username", username);
        Object result = producerTemplate.requestBody(
                getQuery(getDefaultMongoDatabase(), getDefaulMongoCollection(), MongoDbOperation.remove),
                query); //TODO: process DeleteResult

        logger.warn("DELETE: User: {} has been removed from Mongo with the result: {}", username, result);
        logger.debug("DELETE: result: {}", result);
        return OperationResult.SUCCESS;
    }
}
