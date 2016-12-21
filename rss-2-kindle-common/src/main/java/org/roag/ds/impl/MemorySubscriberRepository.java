package org.roag.ds.impl;

import org.roag.ds.OperationResult;
import org.roag.ds.SubscriberRepository;
import org.roag.model.Subscriber;
import org.roag.model.SubscriberStatus;
import org.roag.service.SubscriberFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by eurohlam on 08.12.16.
 */
public class MemorySubscriberRepository implements SubscriberRepository
{
    private Map<String, Subscriber> subscribers;
    private static SubscriberRepository repository;
    private final SubscriberFactory factory;

    private MemorySubscriberRepository()
    {
        this.subscribers = new ConcurrentHashMap<>();
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

    @Override
    public Subscriber getSubscriber(String email) throws Exception
    {
        return subscribers.get(email);
    }

    @Override
    public String getSubscriberAsJSON(String email) throws Exception
    {
        return factory.convertPojo2Json(getSubscriber(email));
    }

    @Override
    public OperationResult updateSubscriber(Subscriber subscriber) throws Exception
    {
        Subscriber s = subscribers.replace(subscriber.getEmail(), subscriber);
        return s != null? OperationResult.SUCCESS: OperationResult.FAILURE;
    }

    @Override
    public OperationResult suspendSubscriber(String email) throws Exception
    {
        Subscriber s = getSubscriber(email);
        if (s != null)
        {
            s.setStatus(SubscriberStatus.SUSPENDED.toString());
            return updateSubscriber(s);
        }
        return OperationResult.NOT_EXIST;
    }

    @Override
    public OperationResult resumeSubscriber(String email) throws Exception
    {
        Subscriber s = getSubscriber(email);
        if (s != null)
        {
            s.setStatus(SubscriberStatus.ACTIVE.toString());
            return updateSubscriber(s);
        }
        return OperationResult.NOT_EXIST;
    }

    @Override
    public OperationResult addSubscriber(Subscriber subscriber) throws Exception
    {
        if (subscribers.containsKey(subscriber.getEmail()))
            throw new IllegalArgumentException("Subscriber " + subscriber.getEmail() + " already exists");
        subscribers.put(subscriber.getEmail(), subscriber);
        return OperationResult.SUCCESS;
    }

    @Override
    public OperationResult removeSubscriber(String email) throws Exception
    {
        Subscriber s= subscribers.remove(email);
        return s!=null?OperationResult.SUCCESS:OperationResult.NOT_EXIST;
    }

    @Override
    public OperationResult removeSubscriber(Subscriber subscriber) throws Exception
    {
        return removeSubscriber(subscriber.getEmail());
    }

    @Override
    public List<Subscriber> findAll() throws Exception
    {
        return new ArrayList<Subscriber>(subscribers.values());
    }

    @Override
    public List<Subscriber> findAll(Map condition) throws Exception
    {
        return null;//TODO: implement findAll with conditions
    }
}
