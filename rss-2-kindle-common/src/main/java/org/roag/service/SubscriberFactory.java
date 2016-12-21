package org.roag.service;

import com.google.gson.Gson;
import org.roag.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by eurohlam on 09.11.16.
 */
public class SubscriberFactory
{
    //default timeout is 24 hours
    final public long DEFAULT_TIMEOUT=24;

    final private DateFormat format = new SimpleDateFormat("yyyy-MM-dd");


    private Gson gson = new Gson();

    public Subscriber newSubscriber(String email, String name, String rss) throws IllegalArgumentException
    {
        return newSubscriber(email, name, new String[]{rss}, new Date(), DEFAULT_TIMEOUT, TimeUnit.HOURS);//TODO: starttime
    }

    public Subscriber newSubscriber(String email, String name, String[] rssList, Date startDate, long timeout, TimeUnit timeUnit) throws IllegalArgumentException
    {
        if (email == null || email.length()==0)
            throw new IllegalArgumentException("Email of new subscriber can not be empty");

        if (rssList == null || rssList.length == 0)
            throw new IllegalArgumentException("New subscriber has to have at least one rss");

        Subscriber s=new Subscriber();
        s.setEmail(email);
        s.setName(name);
        s.setStatus(SubscriberStatus.ACTIVE.toString());

        List<Rss> list = new ArrayList<>(rssList.length);
        for (String rss: rssList)
        {
            Rss rss_list = new Rss();
            rss_list.setRss(rss);
            rss_list.setStatus(RssStatus.ACTIVE.toString());
            list.add(rss_list);
        }
        s.setRsslist(list);
        Settings settings = new Settings();
        settings.setStarttime(format.format(startDate));
        settings.setTimeout(Long.toString(timeUnit != TimeUnit.HOURS ? timeUnit.toHours(timeout):timeout));
        s.setSettings(settings);
        return s;

    }

    public <T> T convertJson2Pojo(Class<T> _class, String source_object)
    {
        T subscr = gson.fromJson(source_object, _class);
        return subscr;
    }

    public String convertPojo2Json(Object pojo)
    {
        return gson.toJson(pojo);
    }

}
