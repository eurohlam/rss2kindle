package org.roag.ds.mongo;

import com.mongodb.WriteResult;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.roag.ds.OperationResult;
import org.roag.ds.SubscriberRepository;
import org.roag.ds.UserRepository;
import org.roag.model.Subscriber;
import org.roag.model.SubscriberStatus;
import org.roag.model.User;
import org.roag.model.UserStatus;
import org.roag.service.SubscriberFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Collections;
import java.util.List;
import java.util.Map;


/**
 * Created by eurohlam on 07.12.16.
 */
public class MongoSubscriberRepository implements SubscriberRepository
{

    final private Logger logger = LoggerFactory.getLogger(MongoSubscriberRepository.class);

    private UserRepository userRepository;
    private ProducerTemplate producerTemplate;
    private MongoHelper mongoHelper;
    private SubscriberFactory subscriberFactory;

    public MongoSubscriberRepository(MongoUserRepository userRepository)
    {
        this(userRepository, userRepository.getMongoHelper(), userRepository.getCamelContext());
    }

    public MongoSubscriberRepository(UserRepository userRepository, MongoHelper mongoHelper, CamelContext context)
    {
        this.userRepository = userRepository;
        this.subscriberFactory = new SubscriberFactory();
        this.mongoHelper = mongoHelper;
        assert context != null;
        this.producerTemplate = context.createProducerTemplate();
    }

    private User getUser(String username) throws Exception
    {
        return userRepository.getUser(username);
    }

    @Override
    public UserRepository getUserRepository() throws Exception {
        return userRepository;
    }

    @Override
    public OperationResult addSubscriber(String username, Subscriber subscriber) throws Exception
    {
        logger.debug("Trying to add new subscriber {} for user {}", subscriber.getEmail(),  username);
        User user = getUser(username);
        user.getSubscribers().add(subscriber);
        OperationResult r = mongoHelper.updateUser(user, producerTemplate);
        logger.info("Added subscriber {} for user {} with the result {}", subscriber.getEmail(), username, r);
        return r;
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
                OperationResult r = mongoHelper.updateUser(user, producerTemplate);
                logger.warn("Removed subscriber {} for user {} with the result {}", email, username, r);
                return r;
            }
        }
//        WriteResult r = mongoHelper.removeSubscriber(subscriber.getEmail(), producerTemplate);
        return OperationResult.NOT_EXIST;
    }

    @Override
    public List<Subscriber> findAllSubscribersByUser(String username) throws Exception {
        logger.debug("Fetch all subscribers for user {} from Mongo", username);

        User user=getUser(username);
        if (user == null)
            throw new IllegalArgumentException("User " + username + " does not exist");

        return user.getSubscribers();
    }

    @Override
    public List<Subscriber> findAllSubscribersByUser(String username, Map condition) throws Exception {
        throw new NotImplementedException();//TODO
    }

    @Override
    public Subscriber getSubscriber(String username, String email) throws Exception
    {
        logger.debug("Fetch subscriber {} from Mongo", email);

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
                OperationResult r = mongoHelper.updateUser(user, producerTemplate);
                logger.info("Updated subscriber {} for user {} with the result {}", subscriber.getEmail(), username, r);
                return r;
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
