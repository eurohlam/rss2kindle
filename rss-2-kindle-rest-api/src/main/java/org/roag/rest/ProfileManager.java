package org.roag.rest;

import com.google.gson.Gson;
import org.roag.ds.OperationResult;
import org.roag.ds.SubscriberRepository;
import org.roag.ds.UserRepository;
import org.roag.model.Rss;
import org.roag.model.RssStatus;
import org.roag.model.Subscriber;
import org.roag.model.User;
import org.roag.service.SubscriberFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by eurohlam on 30.08.17.
 */
@Service
@Path("profile/{username: [a-zA-Z][a-zA-Z_0-9]*}")
public class ProfileManager
{
    final private Logger logger = LoggerFactory.getLogger(ProfileManager.class);

    @Context
    private Request request;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubscriberRepository subscriberRepository;

    private SubscriberFactory subscriberFactory = new SubscriberFactory();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserDetails(@PathParam("username") String username)
    {
        logger.debug("Fetch user details for user {}", username);
        try
        {
            String subscribers=subscriberFactory.convertPojo2Json(userRepository.getUser(username));
            return Response.ok(subscribers, MediaType.APPLICATION_JSON_TYPE).build();
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @GET
    @Path("/subscribers")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllSubscribers(@PathParam("username") String username)
    {
        logger.debug("Fetch all subscribers for user {}", username);
        try
        {
            String subscribers=subscriberFactory.convertPojo2Json(subscriberRepository.findAllSubscribersByUser(username));
            return Response.ok(subscribers, MediaType.APPLICATION_JSON_TYPE).build();
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @GET
    @Path("/{email: \\w+@\\w+\\.[a-zA-Z]{2,}}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSubscriber(@PathParam("username") String username, @PathParam("email") String id)
    {
        logger.debug("Fetch subscriber {} for user {}", id, username);
        try
        {
            String subscriber = subscriberRepository.getSubscriberAsJSON(username, id);
            return Response.ok(subscriber, MediaType.APPLICATION_JSON_TYPE).build();
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @GET
    @Path("/{email: \\w+@\\w+\\.[a-zA-Z]{2,}}/suspend")
    @Produces(MediaType.APPLICATION_JSON)
    public Response suspendSubscriber(@PathParam("username") String username, @PathParam("email") String id)
    {
        logger.warn("Suspend subscriber {} for user {}", id, username);
        try
        {
            OperationResult result= subscriberRepository.suspendSubscriber(username, id);
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
    @Path("/{email: \\w+@\\w+\\.[a-zA-Z]{2,}}/resume")
    @Produces(MediaType.APPLICATION_JSON)
    public Response resumeSubscriber(@PathParam("username") String username, @PathParam("email") String id)
    {
        logger.warn("Resume subscriber {} for user {}", id, username);
        try
        {
            OperationResult result = subscriberRepository.resumeSubscriber(username, id);
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
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addSubscriber(@PathParam("username") String username,
                                  @FormParam("email") String email,
                                  @FormParam("name") String name,
                                  @FormParam("rss") String rss)
    {
        logger.info("Add new subscriber {} for user {}", email, username);
        try
        {
            OperationResult result = subscriberRepository.addSubscriber(username, subscriberFactory.newSubscriber(email, name, rss));
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

/*
    @POST
    @Path("/update")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateSubscriber(@PathParam("username") String username,
                                     @FormParam("email") String email,
                                     @FormParam("name") String name,
                                     @FormParam("rss") String rss)
    {
        logger.warn("Update existing subscriber {} for user {}", email, username);
        try
        {
            OperationResult result = subscriberRepository.updateSubscriber(username, subscriberFactory.newSubscriber(email, name, rss));
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
*/

    @POST
    @Path("/update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateSubscriber(@PathParam("username") String username, String message)
    {
        logger.warn("Requested to update existing subscriber for user {} with data {}", username, message);
        try
        {
            Subscriber subscriber =subscriberFactory.convertJson2Pojo(Subscriber.class, message);
            OperationResult result = subscriberRepository.updateSubscriber(username, subscriber);
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
    @Path("/{email: \\w+@\\w+\\.[a-zA-Z]{2,}}/remove")
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeSubscriber(@PathParam("username") String username, @PathParam("email") String id)
    {
        logger.warn("Remove subscriber {} for user {}", id, username);
        try
        {
            OperationResult  result = subscriberRepository.removeSubscriber(username, id);
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
    @Path("/{email: \\w+@\\w+\\.[a-zA-Z]{2,}}/subscriptions")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllSubscriptions(@PathParam("username") String username, @PathParam("email") String id)
    {
        logger.debug("Fetch all subscriptions for subscriber {} by user {}", id, username);
        try
        {
            Subscriber subscriber = subscriberRepository.getSubscriber(username, id);
            String result=subscriberFactory.convertPojo2Json(subscriber.getRsslist());
            return Response.ok(result, MediaType.APPLICATION_JSON_TYPE).build();
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @POST
    @Path("/{email: \\w+@\\w+\\.[a-zA-Z]{2,}}/subscribe")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addSubscription(@PathParam("username") String username,
                                    @PathParam("email") String id,
                                    @FormParam("rss") String rss)
    {
        logger.info("Add new subscription {} for subscriber {} by user {}", rss, id, username);
        try
        {
            Subscriber subscriber = subscriberRepository.getSubscriber(username, id);
            for (Rss r : subscriber.getRsslist())
                if (r.getRss().equals(rss))
                    return Response.status(Response.Status.CONFLICT).build();

            Rss _rss = new Rss();
            _rss.setRss(rss);
            _rss.setStatus(RssStatus.ACTIVE.toString());
            subscriber.getRsslist().add(_rss);
            OperationResult result = subscriberRepository.updateSubscriber(username, subscriber);
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
    @Path("/{email: \\w+@\\w+\\.[a-zA-Z]{2,}}/unsubscribe")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeSubscription(@PathParam("username") String username,
                                       @PathParam("email") String id,
                                       @FormParam("rss") String rss)
    {
        logger.warn("Remove subscription {} for subscriber {} by user {]", rss, id, username);
        try
        {
            Subscriber subscriber = subscriberRepository.getSubscriber(username, id);
            List<Rss> rssList=subscriber.getRsslist();
            for (int i=0; i< rssList.size(); i++) {
                Rss _rss=rssList.get(i);
                if (_rss.getRss().equals(rss))
                    rssList.remove(i);
            }
            subscriber.setRsslist(rssList);

            OperationResult result = subscriberRepository.updateSubscriber(username, subscriber);
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
}
