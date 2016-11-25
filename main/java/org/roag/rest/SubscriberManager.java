package org.roag.rest;

import com.mongodb.WriteResult;
import org.apache.camel.ProducerTemplate;
import org.roag.camel.ServiceLocator;
import org.roag.mongo.MongoHelper;
import org.roag.mongo.Subscriber;
import org.roag.mongo.SubscriberFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

/**
 * Created by eurohlam on 09.11.16.
 */
@Path("subscribers")
public class SubscriberManager
{
    final public Logger logger = LoggerFactory.getLogger(SubscriberManager.class);

    @Context
    private Request request;

    private ApplicationContext context;

    private ProducerTemplate producerTemplate;

    private MongoHelper mongoHelper;

    private SubscriberFactory subscriberFactory;

    public SubscriberManager()
    {
        super();
        subscriberFactory = new SubscriberFactory();
        context = ServiceLocator.getContext();
        mongoHelper = context.getBean(MongoHelper.class);
        producerTemplate = ServiceLocator.getCamelContext().createProducerTemplate();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSubscriber(@PathParam("id") String id)
    {
        logger.debug("Fetch subscriber {} from Mongo", id);
        try
        {
            Subscriber subscriber = mongoHelper.getSubscriber(id, producerTemplate);
            String subscr = subscriberFactory.convertPojo2Json(subscriber);
            return Response.ok(subscr, MediaType.APPLICATION_JSON_TYPE).build();
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @GET
    @Path("/{id}/suspend")
    @Produces(MediaType.APPLICATION_JSON)
    public void suspendSubscriber(@PathParam("id") String id)
    {
        logger.debug("Suspend subscriber {}", id);
//        Subscriber subscr = mongoHelper.getSubscriber(id, producerTemplate);

    }

    @GET
    @Path("/{id}/resume")
    @Produces(MediaType.APPLICATION_JSON)
    public void resumeSubscriber(@PathParam("id") String id)
    {
        logger.debug("Resume subscriber {}", id);
//        Subscriber subscr = mongoHelper.getSubscriber(id, producerTemplate);

    }

    @POST
    @Path("/new")
    @Consumes("application/x-www-form-urlencoded")
    public Response addSubscriber(@FormParam("email") String email,
                                  @FormParam("name") String name,
                                  @FormParam("rss") String rss,
                                  @FormParam("to") String kindleEmail)
    {
        logger.debug("Add new subscriber {}", email);
        try
        {
            WriteResult r= mongoHelper.addSubscriber(
                    subscriberFactory.newSubscriber(email, name, rss, kindleEmail),
                    producerTemplate);
            return Response.ok(r.toString(), MediaType.TEXT_PLAIN_TYPE).build();
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GET
    @Path("/{id}/remove")
    @Produces(MediaType.TEXT_PLAIN)
    public Response removeSubscriber(@PathParam("id") String id)
    {
        logger.debug("Remove subscriber {}", id);
        try
        {
            WriteResult r=mongoHelper.removeSubscriber(id, producerTemplate);
            return Response.ok(r.toString(), MediaType.TEXT_PLAIN_TYPE).build();
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

}
