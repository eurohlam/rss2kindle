package org.roag.mongo;

import com.google.gson.Gson;
import com.mongodb.DBObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by eurohlam on 09.11.16.
 */
public class SubscriberFactory
{
    final public Logger logger = LoggerFactory.getLogger(SubscriberFactory.class);

    public static enum SUBSCRIBER_STATUS
    {
        ACTIVE("active"),
        SUSPENDED("suspended"),
        TERMINATED("terminated");

        private String value;
        private SUBSCRIBER_STATUS(String value)
        {
            this.value = value;
        }

        @Override
        public String toString()
        {
            return value;
        }

        public static SUBSCRIBER_STATUS fromValue(String value)
        {
            for (SUBSCRIBER_STATUS s:SUBSCRIBER_STATUS.values())
            {
                if (s.toString().equals(value))
                    return s;
            }
            throw new IllegalArgumentException("Illegal value of argument: " + value);
        }
    };

    public static enum RSS_STATUS {ACTIVE, DEAD};


    private Gson gson = new Gson();

    public Subscriber newSubscriber(String email, String name, String rss, String kindleEmail)
    {
        Subscriber s=new Subscriber();
        s.setEmail(email);
        s.setName(name);
        s.setStatus(SUBSCRIBER_STATUS.ACTIVE.toString());

        Rsslist rsslist=new Rsslist();
        rsslist.setRss(rss);
        rsslist.setStatus(RSS_STATUS.ACTIVE.toString());
        List<Rsslist> list = new ArrayList<>(1);
        list.add(rsslist);
        s.setRsslist(list);

        //TODO: settings TBD
        Settings settings = new Settings();
//        settings.setStarttime("");
        settings.setTimeout("24");
        settings.setTo(kindleEmail);
        s.setSettings(settings);
        return s;
    }

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
