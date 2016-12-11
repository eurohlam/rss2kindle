package org.roag.ds;

import org.roag.model.Subscriber;

import java.util.List;
import java.util.Map;

/**
 * Created by eurohlam on 08.12.16.
 */
public interface SubscriberRepository
{
    public Subscriber getSubscriber(String email) throws Exception;

    public String getSubscriberAsJSON(String email) throws Exception;

    public OperationResult updateSubscriber(Subscriber subscriber) throws Exception;

    public OperationResult suspendSubscriber(String email) throws Exception;

    public OperationResult resumeSubscriber(String email) throws Exception;

    public OperationResult addSubscriber(Subscriber subscriber) throws Exception;

    public OperationResult removeSubscriber(String email) throws Exception;

    public OperationResult removeSubscriber(Subscriber subscriber) throws Exception;

    public List<Subscriber> findAll() throws Exception;

    public List<Subscriber> findAll(Map condition) throws Exception;
}
