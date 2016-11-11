package org.roag.mongo;

import com.google.gson.Gson;
import com.mongodb.DBObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by eurohlam on 09.11.16.
 */
public class SubscriberFactory
{
    final public Logger logger = LoggerFactory.getLogger(SubscriberFactory.class);

    private Gson gson = new Gson();

    public Subscriber getSubscriberFromDBObject(DBObject subscriber)
    {
        return convertDBObject2Pojo(Subscriber.class, subscriber);
    }

    public <T> T convertDBObject2Pojo(Class<T> _class, DBObject source_object)
    {
        DBObject obj = source_object;
        obj.removeField("_id");

        String s = gson.toJson(obj);
        logger.debug(s);
        T subscr = gson.fromJson(s, _class);
        return subscr;
    }

    public String convertPojo2Json(Object pojo)
    {
        return gson.toJson(pojo);
    }

}
