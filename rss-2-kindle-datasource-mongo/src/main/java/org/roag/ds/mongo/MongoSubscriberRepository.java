package org.roag.ds.mongo;

import com.mongodb.WriteResult;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.roag.ds.OperationResult;
import org.roag.ds.SubscriberRepository;
import org.roag.model.Subscriber;
import org.roag.model.SubscriberStatus;
import org.roag.model.User;
import org.roag.model.UserStatus;
import org.roag.service.SubscriberFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;


/**
 * Created by eurohlam on 07.12.16.
 */
public class MongoSubscriberRepository implements SubscriberRepository
{

    final private Logger logger = LoggerFactory.getLogger(MongoSubscriberRepository.class);


    private ProducerTemplate producerTemplate;

    private MongoHelper mongoHelper;

    private SubscriberFactory subscriberFactory;

    public MongoSubscriberRepository(MongoHelper mongoHelper, CamelContext context)
    {
        this.subscriberFactory = new SubscriberFactory();
        this.mongoHelper = mongoHelper;
        assert context != null;
        this.producerTemplate = context.createProducerTemplate();
    }

    @Override
    public User getUser(String username) throws Exception {
        logger.debug("Fetch user {} from Mongo", username);
        User user = mongoHelper.getUser(username, producerTemplate);
        return user;
    }

    @Override
    public OperationResult addUser(User user) throws Exception {
        logger.debug("Add new user {}", user.getUsername());
        WriteResult r = mongoHelper.addUser(user, producerTemplate);
        logger.info("Added user {} with the result {}", user.getUsername(), r.toString().replaceFirst("WriteResult", ""));
        return OperationResult.SUCCESS;
    }

    @Override
    public OperationResult updateUser(User user) throws Exception {
        logger.debug("Update user {}", user.getUsername());
        WriteResult r = mongoHelper.updateUser(user, producerTemplate);
        logger.info("Updated user {} with the result {}", user.getUsername(), r.toString().replaceFirst("WriteResult", ""));
        return r.getN()>0?OperationResult.SUCCESS:OperationResult.NOT_EXIST;
    }

    @Override
    public OperationResult removeUser(String username) throws Exception { //TODO: mongo remove user
        return null;
    }

    @Override
    public OperationResult lockUser(String username) throws Exception {
        User user= getUser(username);
        user.setStatus(UserStatus.LOCKED.toString());
        logger.warn("Trying to lock user {}", username);
        return updateUser(user);
    }

    @Override
    public OperationResult unlockUser(String username) throws Exception {
        User user= getUser(username);
        user.setStatus(UserStatus.ACTIVE.toString());
        logger.warn("Trying to activate user {}", username);
        return updateUser(user);
    }

    @Override
    public OperationResult addSubscriber(String username, Subscriber subscriber) throws Exception
    {
        logger.debug("Trying to add new subscriber {} for user {}", subscriber.getEmail(),  username);
        User user = getUser(username);
        user.getSubscribers().add(subscriber);
        WriteResult r = mongoHelper.updateUser(user, producerTemplate);
        logger.info("Added subscriber {} for user {} with the result {}", subscriber.getEmail(), username, r.toString().replaceFirst("WriteResult", ""));
        return OperationResult.SUCCESS;
    }

    @Override
    public OperationResult removeSubscriber(String username, Subscriber subscriber) throws Exception
    {
        return removeSubscriber(username, subscriber.getEmail());
    }

    @Override
    public OperationResult removeSubscriber(String username, String email) throws Exception
    {
        logger.debug("Trying to remove subscriber {} for user {}", email, username);
        User user = getUser(username);
        for (short i=0; i<user.getSubscribers().size(); i++)
        {
            Subscriber s = user.getSubscribers().get(i);
            if (s.getEmail().equals(email)) {
                user.getSubscribers().remove(i);
                WriteResult r = mongoHelper.updateUser(user, producerTemplate);
                logger.warn("Removed subscriber {} for user {} with the result {}", email, username, r.toString().replaceFirst("WriteResult", ""));
                return r.getN()>0?OperationResult.SUCCESS:OperationResult.NOT_EXIST;
            }
        }
//        WriteResult r = mongoHelper.removeSubscriber(subscriber.getEmail(), producerTemplate);
        return OperationResult.NOT_EXIST;
    }

    @Override
    public List<User> findAll() throws Exception
    {
        return findAll(Collections.EMPTY_MAP);
    }

    @Override
    public List<User> findAll(Map condition) throws Exception
    {
        logger.debug("Fetch all users from Mongo by condition {}", condition);
        List<User> users = mongoHelper.findAllByCondition(producerTemplate, condition);
        return users;
    }


    @Override
    public List<Subscriber> findAllSubscribersByUser(String username) throws Exception {
        return null;//TODO
    }

    @Override
    public List<Subscriber> findAllSubscribersByUser(String username, Map condition) throws Exception {
        return null;//TODO
    }

    @Override
    public Subscriber getSubscriber(String username, String email) throws Exception
    {
        logger.debug("Fetch subscriber {} from Mongo", email);
        Subscriber subscriber = mongoHelper.getSubscriber(username, email, producerTemplate);
        return subscriber;
    }

    @Override
    public String getSubscriberAsJSON(String username, String email) throws Exception
    {
        return subscriberFactory.convertPojo2Json(getSubscriber(username, email));
    }

    @Override
    public OperationResult updateSubscriber(String username, Subscriber subscriber) throws Exception
    {
        logger.debug("Update subscriber {} for user {}", subscriber.getEmail(), username);
        User user = getUser(username);
        for (short i=0; i<user.getSubscribers().size(); i++)
        {
            Subscriber s = user.getSubscribers().get(i);
            if (s.getEmail().equals(subscriber.getEmail())) {
                user.getSubscribers().remove(i);
                user.getSubscribers().add(i,subscriber);
                WriteResult r = mongoHelper.updateUser(user, producerTemplate);
                logger.info("Updated subscriber {} for user {} with the result {}", subscriber.getEmail(), username, r.toString().replaceFirst("WriteResult", ""));
                return r.getN()>0?OperationResult.SUCCESS:OperationResult.NOT_EXIST;
            }
        }
//        WriteResult r = mongoHelper.updateSubscriber(subscriber, producerTemplate);
        return OperationResult.NOT_EXIST;
    }

    @Override
    public OperationResult suspendSubscriber(String username, String id) throws Exception
    {
        logger.warn("Trying to suspend subscriber {} for user {}", id, username);
        Subscriber subscriber = getSubscriber(username, id);
        if (SubscriberStatus.fromValue(subscriber.getStatus()) == SubscriberStatus.TERMINATED)
        {
            logger.error("Subscriber {} can't be suspended due to it has been terminated", id);
            return OperationResult.FAILURE;
        }
        else
        {
            subscriber.setStatus(SubscriberStatus.SUSPENDED.toString());
            return updateSubscriber(username, subscriber);
        }
    }

    @Override
    public OperationResult resumeSubscriber(String username, String id) throws Exception
    {
        logger.warn("Trying to resume subscriber {} for user {}", id, username);
        Subscriber subscriber = getSubscriber(username, id);
        if (SubscriberStatus.fromValue(subscriber.getStatus()) == SubscriberStatus.TERMINATED)
        {
            logger.error("Subscriber {} can't be resumed due to it has been terminated", id);
            return OperationResult.FAILURE;
        }
        else
        {
            subscriber.setStatus(SubscriberStatus.ACTIVE.toString());
            return updateSubscriber(username, subscriber);
        }
    }

}
