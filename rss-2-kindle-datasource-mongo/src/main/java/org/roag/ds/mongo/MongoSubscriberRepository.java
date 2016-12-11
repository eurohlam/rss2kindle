package org.roag.ds.mongo;

import com.mongodb.WriteResult;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.roag.ds.OperationResult;
import org.roag.ds.SubscriberRepository;
import org.roag.model.Subscriber;
import org.roag.service.SubscriberFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;


/**
 * Created by eurohlam on 07.12.16.
 */
@Component
@Scope("prototype")
@Lazy
public class MongoSubscriberRepository implements SubscriberRepository
{

    final private Logger logger = LoggerFactory.getLogger(MongoSubscriberRepository.class);


    private ProducerTemplate producerTemplate;

    private MongoHelper mongoHelper;

    private SubscriberFactory subscriberFactory;

    @Autowired
    public MongoSubscriberRepository(MongoHelper mongoHelper, ApplicationContext context)
    {
        this.subscriberFactory = new SubscriberFactory();
        this.mongoHelper = mongoHelper;
        CamelContext camel=context.getBean(CamelContext.class);
        assert camel != null;
        this.producerTemplate = camel.createProducerTemplate();
    }

    @Override
    public OperationResult addSubscriber(Subscriber subscriber) throws Exception
    {
        logger.debug("Add new subscriber {}", subscriber.getEmail());
        WriteResult r = mongoHelper.addSubscriber(subscriber, producerTemplate);
        logger.debug("Added subscriber {} with the result {}", subscriber.getEmail(), r.toString().replaceFirst("WriteResult", ""));
        return OperationResult.SUCCESS;
    }

    @Override
    public OperationResult removeSubscriber(Subscriber subscriber) throws Exception
    {
        logger.debug("Remove subscriber {}", subscriber.getEmail());
        WriteResult r = mongoHelper.removeSubscriber(subscriber.getEmail(), producerTemplate);
        logger.debug("Removed subscriber {} with the result {}", subscriber.getEmail(), r.toString().replaceFirst("WriteResult", ""));
        return r.getN()>0?OperationResult.SUCCESS:OperationResult.NOT_EXIST;
    }

    @Override
    public OperationResult removeSubscriber(String email) throws Exception
    {
        Subscriber s = new Subscriber();
        s.setEmail(email);
        return removeSubscriber(s);
    }

    @Override
    public List<Subscriber> findAll() throws Exception
    {
        return findAll(Collections.EMPTY_MAP);
    }

    @Override
    public List<Subscriber> findAll(Map condition) throws Exception
    {
        logger.debug("Fetch all subscriber from Mongo by condition {}", condition);
        List<Subscriber> subscribers = mongoHelper.getSubscribers(producerTemplate, condition);
        return subscribers;
    }

    @Override
    public Subscriber getSubscriber(String email) throws Exception
    {
        logger.debug("Fetch subscriber {} from Mongo", email);
        Subscriber subscriber = mongoHelper.getSubscriber(email, producerTemplate);
        return subscriber;
    }

    @Override
    public String getSubscriberAsJSON(String email) throws Exception
    {
        return subscriberFactory.convertPojo2Json(getSubscriber(email));
    }

    @Override
    public OperationResult updateSubscriber(Subscriber subscriber) throws Exception
    {
        logger.debug("Update subscriber {}", subscriber.getEmail());
        return OperationResult.FAILURE;
    }

    @Override
    public OperationResult suspendSubscriber(String id) throws Exception
    {
        logger.debug("Suspend subscriber {}", id);
        return OperationResult.FAILURE;
    }

    @Override
    public OperationResult resumeSubscriber(String id) throws Exception
    {
        logger.debug("Resume subscriber {}", id);
        return OperationResult.FAILURE;
    }

}
