package org.roag.ds.impl;

import org.roag.ds.OperationResult;
import org.roag.ds.SubscriberRepository;
import org.roag.ds.UserRepository;
import org.roag.model.Subscriber;
import org.roag.model.SubscriberStatus;
import org.roag.model.User;
import org.roag.service.SubscriberFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by eurohlam on 08.12.16.
 */
@Service
public class MemorySubscriberRepository implements SubscriberRepository {
    private static SubscriberRepository repository;

    private UserRepository userRepository;
    private final SubscriberFactory factory;

    private MemorySubscriberRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.factory = new SubscriberFactory();
    }

    public static SubscriberRepository getInstance(UserRepository userRepository) {
        ReentrantReadWriteLock.WriteLock lock = new ReentrantReadWriteLock().writeLock();
        lock.lock();
        try {
            if (repository == null)
                repository = new MemorySubscriberRepository(userRepository);
            return repository;
        } finally {
            lock.unlock();
        }
    }

    private User getUser(String username) throws Exception {
        return userRepository.getUser(username);
    }

    @Override
    public UserRepository getUserRepository() throws Exception {
        return userRepository;
    }

    @Override
    public Subscriber getSubscriber(String username, String email) throws Exception {
        User user = getUser(username);
        if (user == null)
            throw new IllegalArgumentException("User " + username + " does not exist");

        Optional<Subscriber> subscriber= user.getSubscribers().stream().filter(s -> s.getEmail().equals(email)).findFirst();
        return subscriber.isPresent() ? subscriber.get() : null;
    }

    @Override
    public String getSubscriberAsJSON(String username, String email) throws Exception {
        return factory.convertPojo2Json(getSubscriber(username, email));
    }

    @Override
    public OperationResult updateSubscriber(String username, Subscriber subscriber) throws Exception {
        if (username == null || subscriber == null)
            return OperationResult.FAILURE;

        User user = getUser(username);
        for (Subscriber s: user.getSubscribers()) {
            if (s.getEmail().equals(subscriber.getEmail())) {
                user.getSubscribers().remove(s);
                user.getSubscribers().add(subscriber);
                return OperationResult.SUCCESS;
            }
        }

        return OperationResult.NOT_EXIST;
    }

    @Override
    public OperationResult suspendSubscriber(String username, String email) throws Exception {
        Subscriber s = getSubscriber(username, email);
        if (s != null) {
            s.setStatus(SubscriberStatus.SUSPENDED.toString());
            return updateSubscriber(username, s);
        }
        return OperationResult.NOT_EXIST;
    }

    @Override
    public OperationResult resumeSubscriber(String username, String email) throws Exception {
        Subscriber s = getSubscriber(username, email);
        if (s != null) {
            s.setStatus(SubscriberStatus.ACTIVE.toString());
            return updateSubscriber(username, s);
        }
        return OperationResult.NOT_EXIST;
    }

    @Override
    public OperationResult addSubscriber(String username, Subscriber subscriber) throws Exception {
        if (getUser(username) == null)
            throw new IllegalArgumentException("User " + username + " does not exist");

        if (getSubscriber(username, subscriber.getEmail()) != null)
            throw new IllegalArgumentException("Subscriber " + subscriber.getEmail() + " for user " + username + " already exists");

        getUser(username).getSubscribers().add(subscriber);

        return OperationResult.SUCCESS;
    }

    @Override
    public OperationResult removeSubscriber(String username, String email) throws Exception {
        User user = getUser(username);

        for (int i = 0; i < user.getSubscribers().size(); i++) {
            Subscriber oldSubscriber = user.getSubscribers().get(i);
            if (oldSubscriber.getEmail().equals(email))
                return user.getSubscribers().remove(i) != null ? OperationResult.SUCCESS : OperationResult.FAILURE;
        }
        return OperationResult.NOT_EXIST;
    }

    @Override
    public OperationResult removeSubscriber(String username, Subscriber subscriber) throws Exception {
        return removeSubscriber(username, subscriber.getEmail());
    }

    @Override
    public List<Subscriber> findAllSubscribersByUser(String username) throws Exception {
        return getUser(username).getSubscribers();
    }

    @Override
    public List<Subscriber> findAllSubscribersByUser(String username, Map condition) throws Exception {
        return findAllSubscribersByUser(username);//TODO: implement findAll with conditions
    }

}
