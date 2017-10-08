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

    public OperationResult removeUser(String username) throws Exception;

    public OperationResult lockUser(String username) throws Exception;

    public OperationResult unlockUser(String username) throws Exception;

    public Subscriber getSubscriber(String username, String email) throws Exception;

    public String getSubscriberAsJSON(String username, String email) throws Exception;

    public OperationResult updateSubscriber(String username, Subscriber subscriber) throws Exception;

    public OperationResult suspendSubscriber(String username, String email) throws Exception;

    public OperationResult resumeSubscriber(String username, String email) throws Exception;

    public OperationResult addSubscriber(String username, Subscriber subscriber) throws Exception;

    public OperationResult removeSubscriber(String username, String email) throws Exception;

    public OperationResult removeSubscriber(String username, Subscriber subscriber) throws Exception;

    public List<Subscriber> findAllSubscribersByUser(String username) throws Exception;

    public List<Subscriber> findAllSubscribersByUser(String username, Map condition) throws Exception;

    public List<User> findAll() throws Exception;

    public List<User> findAll(Map condition) throws Exception;
}
