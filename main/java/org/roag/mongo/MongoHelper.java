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
import java.util.List;
import java.util.Map;

/**
 * Created by eurohlam on 05.10.16.
 */
public class MongoHelper
{
    final public static Logger logger= LoggerFactory.getLogger(MongoHelper.class);

    private String MONGO_SPRING_BEAN;

    private String MONGO_CAMEL_ROUTE;

    private String defaultMongoDatabase;

    private String defaulMongoCollection;

    private MongoHelper()
    {
    }

    public MongoHelper(String mongoBean, String mongoCamelRoute)
    {
        this(mongoBean,mongoCamelRoute, null, null);
    }

    public MongoHelper(String mongoBean, String mongoCamelRoute, String mongoDatabase, String mongoCollection)
    {
        this.MONGO_SPRING_BEAN = mongoBean;
        this.MONGO_CAMEL_ROUTE = mongoCamelRoute;
        this.defaultMongoDatabase = mongoDatabase;
        this.defaulMongoCollection = mongoCollection;
    }

    public String getDefaultMongoDatabase()
    {
        return defaultMongoDatabase;
    }

    public List<DBObject> findAllByCondition(ProducerTemplate producerTemplate, Map<String, String> conditions)
    {
        return findAllByCondition(defaultMongoDatabase, defaulMongoCollection, producerTemplate, conditions);
    }

    public List<DBObject> findAllByCondition(String mongoDatabase, String collection, ProducerTemplate producerTemplate, Map<String, String> conditions)
    {
        DBObject query = BasicDBObjectBuilder.start(conditions).get();
        List<DBObject> result  = producerTemplate.requestBody(findAllQuery(mongoDatabase, collection), query, List.class);
        //TODO: remove this
        if (logger.isDebugEnabled())
        {
            for (DBObject obj:result)
            {
                for (String key : obj.keySet())
                    logger.debug("key = " + key + "   value = " + obj.get(key));
            }
        }
        return result;
    }

    public String findAllQuery(String mongoDatabase, String collection)
    {
        return "mongodb:" + MONGO_SPRING_BEAN + "?database=" + mongoDatabase + "&collection=" + collection+ "&operation=findAll";
    }

    public String findAllQuery()
    {
        return findAllQuery(defaultMongoDatabase, defaulMongoCollection);
    }

    public String findOneByQuery(String mongoDatabase, String collection)
    {
        return "mongodb:" + MONGO_SPRING_BEAN + "?database=" + mongoDatabase + "&collection=" + collection+ "&operation=findOneByQuery";
    }

    public String findOneByQuery()
    {
        return findOneByQuery(defaultMongoDatabase, defaulMongoCollection);
    }

    public <T>T convertDBObject2Pojo(Class<T> _class, DBObject source_object)
    {
        DBObject obj=source_object;
        obj.removeField("_id");

        Gson gson=new Gson();
        String s=gson.toJson(obj);
        logger.debug(s);
        T subscr=gson.fromJson(s, _class);
        return subscr;
    }

    public List<Subscriber> getSubscribers(ProducerTemplate producerTemplate) throws Exception
    {
        DBObject query = BasicDBObjectBuilder.start("status", "active").get();
        List<DBObject> result = producerTemplate.requestBody(MONGO_CAMEL_ROUTE, query, List.class);

        List<Subscriber> subscribers = new ArrayList<>(result.size());
        for (DBObject obj : result)
        {

            Subscriber subscr = convertDBObject2Pojo(Subscriber.class, obj);
            subscribers.add(subscr);
            BasicDBList rsslist = (BasicDBList) obj.get("rsslist");
            logger.info("Subscriber: " + subscr.getEmail() + "\n" + rsslist.getClass().toString() + "  " + rsslist);
        }
        return subscribers;
    }
}
