package org.roag.ds;

import org.roag.model.Subscriber;
import org.roag.model.User;

import java.util.List;
import java.util.Map;

/**
 * Created by eurohlam on 08.12.16.
 */
public interface SubscriberRepository
{
    public User getUser(String username) throws Exception;

    public OperationResult addUser(User user) throws Exception;

    public OperationResult updateUser(User user) throws Exception;

    public Subscriber getSubscriber(String username, String email) throws Exception;

    public String getSubscriberAsJSON(String username, String email) throws Exception;

    public OperationResult updateSubscriber(User user, Subscriber subscriber) throws Exception;

    public OperationResult suspendSubscriber(String username, String email) throws Exception;

    public OperationResult resumeSubscriber(String username, String email) throws Exception;

    public OperationResult addSubscriber(User user, Subscriber subscriber) throws Exception;

    public OperationResult removeSubscriber(String username, String email) throws Exception;

    public OperationResult removeSubscriber(User user, Subscriber subscriber) throws Exception;

    public List<Subscriber> findAll() throws Exception;

    public List<Subscriber> findAll(Map condition) throws Exception;
}
