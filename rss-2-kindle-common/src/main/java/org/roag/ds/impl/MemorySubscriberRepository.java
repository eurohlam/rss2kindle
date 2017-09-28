package org.roag.ds.impl;

import org.roag.ds.OperationResult;
import org.roag.ds.SubscriberRepository;
import org.roag.model.Subscriber;
import org.roag.model.SubscriberStatus;
import org.roag.model.User;
import org.roag.model.UserStatus;
import org.roag.service.SubscriberFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by eurohlam on 08.12.16.
 */
public class MemorySubscriberRepository implements SubscriberRepository
{
    private Map<String, User> users;
//    private Map<String, Subscriber> subscribers;
    private static SubscriberRepository repository;
    private final SubscriberFactory factory;

    private MemorySubscriberRepository()
    {
        this.users = new ConcurrentHashMap<>();
//        this.subscribers = new ConcurrentHashMap<>();
        this.factory = new SubscriberFactory();
    }

    public static SubscriberRepository getInstance()
    {
        ReentrantReadWriteLock.WriteLock lock = new ReentrantReadWriteLock().writeLock();
        lock.lock();
        try
        {
            if (repository == null)
                repository = new MemorySubscriberRepository();
            return repository;
        }
        finally
        {
            lock.unlock();
        }
    }

//    public void setSubscribers(Map<String, Subscriber> subscribers)
//    {
//        this.subscribers=subscribers;
//    }

    @Override
    public Subscriber getSubscriber(String username, String email) throws Exception
    {
        User user=getUser(username);
        if (user == null)
            throw new IllegalArgumentException("User " + username + " does not exist");

        for (Subscriber s: user.getSubscribers())
            if (s.getEmail().equals(email))
                return s;

        return null;
    }

    @Override
    public String getSubscriberAsJSON(String username, String email) throws Exception
    {
        return factory.convertPojo2Json(getSubscriber(username, email));
    }

    @Override
    public OperationResult updateSubscriber(String username, Subscriber subscriber) throws Exception
    {
        User user=getUser(username);

        for (int i=0; i<user.getSubscribers().size(); i++)
        {
            Subscriber oldSubscriber =user.getSubscribers().get(i);
            if (oldSubscriber.getEmail().equals(subscriber.getEmail()))
                user.getSubscribers().remove(i);
        }
        Boolean result = user.getSubscribers().add(subscriber);
        return result ? OperationResult.SUCCESS: OperationResult.FAILURE;
    }

    @Override
    public OperationResult suspendSubscriber(String username, String email) throws Exception
    {
        Subscriber s = getSubscriber(username, email);
        if (s != null)
        {
            s.setStatus(SubscriberStatus.SUSPENDED.toString());
            return updateSubscriber(username, s);
        }
        return OperationResult.NOT_EXIST;
    }

    @Override
    public OperationResult resumeSubscriber(String username, String email) throws Exception
    {
        Subscriber s = getSubscriber(username, email);
        if (s != null)
        {
            s.setStatus(SubscriberStatus.ACTIVE.toString());
            return updateSubscriber(username, s);
        }
        return OperationResult.NOT_EXIST;
    }

    @Override
    public OperationResult addSubscriber(String username, Subscriber subscriber) throws Exception
    {
        if (!users.containsKey(username))
            throw new IllegalArgumentException("User " + username + " does not exist");

        if (getSubscriber(username, subscriber.getEmail())!= null)
            throw new IllegalArgumentException("Subscriber " + subscriber.getEmail() + " for user " + username + " already exists");

        users.get(username).getSubscribers().add(subscriber);

        return OperationResult.SUCCESS;
    }

    @Override
    public OperationResult removeSubscriber(String username, String email) throws Exception
    {
        User user=getUser(username);

        for (int i=0; i<user.getSubscribers().size(); i++)
        {
            Subscriber oldSubscriber =user.getSubscribers().get(i);
            if (oldSubscriber.getEmail().equals(email))
                return user.getSubscribers().remove(i) !=null?OperationResult.SUCCESS:OperationResult.FAILURE;
        }
        return OperationResult.NOT_EXIST;
    }

    @Override
    public OperationResult removeSubscriber(String username, Subscriber subscriber) throws Exception
    {
        return removeSubscriber(username, subscriber.getEmail());
    }

    @Override
    public List<Subscriber> findAll(String username) throws Exception
    {
        return new ArrayList<Subscriber>(getUser(username).getSubscribers());
    }

    @Override
    public List<Subscriber> findAll(String username, Map condition) throws Exception
    {
        return null;//TODO: implement findAll with conditions
    }

    @Override
    public OperationResult addUser(User user) throws Exception
    {
        if (users.containsKey(user.getUsername()))
            throw new IllegalArgumentException("User " + user.getUsername() + " already exists");
        users.put(user.getUsername(), user);
        return OperationResult.SUCCESS;
    }

    @Override
    public User getUser(String username) throws Exception {
        return users.get(username);
    }

    @Override
    public OperationResult updateUser(User user) throws Exception {
        User u = users.replace(user.getUsername(), user);
        return u!=null? OperationResult.SUCCESS: OperationResult.FAILURE;
    }

    @Override
    public OperationResult removeUser(String username) throws Exception {
        User u = users.remove(username);
        return u!=null? OperationResult.SUCCESS: OperationResult.FAILURE;
    }

    @Override
    public OperationResult lockUser(String username) throws Exception {
        User user = getUser(username);
        user.setStatus(UserStatus.LOCKED.toString());
        return updateUser(user);
    }

    @Override
    public OperationResult unlockUser(String username) throws Exception {
        User user = getUser(username);
        user.setStatus(UserStatus.ACTIVE.toString());
        return updateUser(user);
    }
}
