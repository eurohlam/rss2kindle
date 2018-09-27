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
    UserRepository getUserRepository() throws Exception;

    Subscriber getSubscriber(String username, String email) throws Exception;

    OperationResult updateSubscriber(String username, Subscriber subscriber) throws Exception;

    OperationResult suspendSubscriber(String username, String email) throws Exception;

    OperationResult resumeSubscriber(String username, String email) throws Exception;

    OperationResult addSubscriber(String username, Subscriber subscriber) throws Exception;

    OperationResult removeSubscriber(String username, String email) throws Exception;

    OperationResult removeSubscriber(String username, Subscriber subscriber) throws Exception;

    List<Subscriber> findAllSubscribersByUser(String username) throws Exception;

    List<Subscriber> findAllSubscribersByUser(String username, Map condition) throws Exception;
}
