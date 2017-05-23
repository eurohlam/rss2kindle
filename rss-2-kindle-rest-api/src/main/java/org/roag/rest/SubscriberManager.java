package org.roag.rest;

import org.roag.ds.OperationResult;
import org.roag.ds.SubscriberRepository;
import org.roag.service.SubscriberFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

/**
 * Created by eurohlam on 09.11.16.
 */
@Service
@Path("subscribers")
public class SubscriberManager
{
    final private Logger logger = LoggerFactory.getLogger(SubscriberManager.class);

    @Context
    private Request request;

    @Autowired
    private SubscriberRepository subscriberRepository;

    private SubscriberFactory subscriberFactory;

    public SubscriberManager()
    {
        super();
        this.subscriberFactory = new SubscriberFactory();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllSubscribers()
    {
        logger.debug("Fetch all subscribers from Mongo");
        try
        {
            String subscribers=subscriberFactory.convertPojo2Json(subscriberRepository.findAll());
            return Response.ok(subscribers, MediaType.APPLICATION_JSON_TYPE).build();
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSubscriber(@PathParam("id") String id)
    {
        logger.debug("Fetch subscriber {} from Mongo", id);
        try
        {
            String subscriber = subscriberRepository.getSubscriberAsJSON(id);
            return Response.ok(subscriber, MediaType.APPLICATION_JSON_TYPE).build();
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
    public Response suspendSubscriber(@PathParam("id") String id)
    {
        logger.debug("Suspend subscriber {}", id);
        try
        {
            OperationResult result= subscriberRepository.suspendSubscriber(id);
            if (result == OperationResult.SUCCESS)
                return Response.ok(result.toJSON(), MediaType.APPLICATION_JSON_TYPE).build();
            else
                return Response.status(Response.Status.NOT_FOUND).build();
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GET
    @Path("/{id}/resume")
    @Produces(MediaType.APPLICATION_JSON)
    public Response resumeSubscriber(@PathParam("id") String id)
    {
        logger.debug("Resume subscriber {}", id);
        try
        {
            OperationResult result = subscriberRepository.resumeSubscriber(id);
            if (result == OperationResult.SUCCESS)
                return Response.ok(result.toJSON(), MediaType.APPLICATION_JSON_TYPE).build();
            else
                return Response.status(Response.Status.NOT_FOUND).build();
        }
        catch (Exception e)
        {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @POST
    @Path("/new")
    @Consumes("application/x-www-form-urlencoded")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addSubscriber(@FormParam("email") String email,
                                  @FormParam("name") String name,
                                  @FormParam("rss") String rss)
    {
        logger.debug("Add new subscriber {}", email);
        try
        {
            OperationResult result = subscriberRepository.addSubscriber(subscriberFactory.newSubscriber(email, name, rss));
            logger.info(result.toString());
            if (result == OperationResult.SUCCESS)
                return Response.ok(result.toJSON(), MediaType.APPLICATION_JSON_TYPE).build();
            else
                return Response.status(Response.Status.CONFLICT).build();
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @POST
    @Path("/update")
    @Consumes("application/x-www-form-urlencoded")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateSubscriber(@FormParam("email") String email,
                                  @FormParam("name") String name,
                                  @FormParam("rss") String rss)
    {
        logger.debug("Update existing subscriber {}", email);
        try
        {
            OperationResult result = subscriberRepository.updateSubscriber(subscriberFactory.newSubscriber(email, name, rss));
            if (result == OperationResult.SUCCESS)
                return Response.ok(result.toJSON(), MediaType.APPLICATION_JSON_TYPE).build();
            else
                return Response.status(Response.Status.CONFLICT).build();
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GET
    @Path("/{id}/remove")
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeSubscriber(@PathParam("id") String id)
    {
        logger.debug("Remove subscriber {}", id);
        try
        {
            OperationResult  result = subscriberRepository.removeSubscriber(id);
            if (result == OperationResult.SUCCESS)
                return Response.ok(result.toJSON(), MediaType.APPLICATION_JSON_TYPE).build();
            else
                return Response.status(Response.Status.NOT_FOUND).build();
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

}
