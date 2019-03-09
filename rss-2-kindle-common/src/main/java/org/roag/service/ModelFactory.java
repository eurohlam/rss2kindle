package org.roag.service;

import com.google.gson.Gson;
import org.roag.model.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Created by eurohlam on 09.11.16.
 */
public class ModelFactory {
    //default timeout is 24 hours
    public static final long DEFAULT_TIMEOUT = 24;

    private Gson gson;

    public ModelFactory() {
        gson = new Gson();
    }

    public User newUser(String username, String email, String password) {
        Set<Roles> roles = new HashSet<>(1);
        roles.add(Roles.ROLE_USER);
        return newUser(username, email, password, LocalDateTime.now().toString(), UserStatus.ACTIVE, roles, new ArrayList<Subscriber>(3));
    }

    public User newUser(String username, String email, String password, String dateCreated, UserStatus status, Set<Roles> roles, List<Subscriber> subscribers) {
        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        user.setStatus(status.toString());
        user.setDateCreated(dateCreated);
        user.setDateModified(dateCreated);
        user.setRoles(roles);
        user.setSubscribers(subscribers);

        return user;
    }

    public Subscriber newSubscriber(String email, String name, String rss) {
        return newSubscriber(email, name, new String[]{rss}, LocalDateTime.now(), DEFAULT_TIMEOUT, TimeUnit.HOURS);//TODO: starttime
    }

    public Subscriber newSubscriber(String email, String name, String[] rssList, LocalDateTime startDate, long timeout, TimeUnit timeUnit) {
        if (email == null || email.length() == 0)
            throw new IllegalArgumentException("Email of new subscriber can not be empty");

        if (rssList == null || rssList.length == 0)
            throw new IllegalArgumentException("New subscriber has to have at least one rss");

        Subscriber s = new Subscriber();
        s.setEmail(email);
        s.setName(name);
        s.setStatus(SubscriberStatus.ACTIVE.toString());
        s.setDateCreated(LocalDateTime.now().toString());

        List<Rss> list = new ArrayList<>(rssList.length);
        for (String rss : rssList) {
            Rss rss_list = new Rss();
            rss_list.setRss(rss);
            rss_list.setStatus(RssStatus.ACTIVE.toString());
            list.add(rss_list);
        }
        s.setRsslist(list);
        Settings settings = new Settings();
        settings.setStarttime(startDate.toString());
        settings.setTimeout(Long.toString(timeUnit != TimeUnit.HOURS ? timeUnit.toHours(timeout) : timeout));
        s.setSettings(settings);
        return s;

    }

    public User newUser(Consumer<User> consumer) {
        User user=new User();
        consumer.accept(user);
        return user;
    }

    public Subscriber newSubscriber(Consumer<Subscriber> consumer) {
        Subscriber subscriber=new Subscriber();
        consumer.accept(subscriber);
        return subscriber;
    }

    public <T> T json2Pojo(Class<T> _class, String source_object) {
        T subscr = gson.fromJson(source_object, _class);
        return subscr;
    }

    public String pojo2Json(Object pojo) {
        return gson.toJson(pojo);
    }

}
