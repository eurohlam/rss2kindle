package org.roag.mongo;

import com.google.gson.Gson;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.PropertyInject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by eurohlam on 05.10.16.
 */
@Component("mongoHelper")
@Scope("prototype")
@SuppressWarnings("unused")
public class MongoHelper
{
    final public static Logger logger= LoggerFactory.getLogger(MongoHelper.class);

    public static final String MONGO_BEAN="mongoBean";

    @PropertyInject("mongodb.host")
    private String mongoHost;

    @PropertyInject("mongodb.port")
    private String mongoPort;

    @PropertyInject("mongodb.database")
    private String mongoDatabase;

    @PropertyInject("mongodb.collection.name")
    private String mongoCollection;

    public String getMongoHost()
    {
        return mongoHost;
    }

    public String getMongoPort()
    {
        return mongoPort;
    }

    public String getMongoDatabase()
    {
        return mongoDatabase;
    }

    public List<DBObject> findAllByCondition(ProducerTemplate producerTemplate, Map<String, String> conditions)
    {
        return findAllByCondition(mongoDatabase, mongoCollection, producerTemplate, conditions);
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
        return "mongodb:" + MONGO_BEAN + "?database=" + mongoDatabase + "&collection=" + collection+ "&operation=findAll";
    }

    public String findAllQuery()
    {
        return findAllQuery(mongoDatabase, mongoCollection);
    }

    public String findOneByQuery(String mongoDatabase, String collection)
    {
        return "mongodb:" + MONGO_BEAN + "?database=" + mongoDatabase + "&collection=" + collection+ "&operation=findOneByQuery";
    }

    public String findOneByQuery()
    {
        return findOneByQuery(mongoDatabase, mongoCollection);
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
        List<DBObject> result = producerTemplate.requestBody("direct:mongoFindAll", query, List.class);

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
