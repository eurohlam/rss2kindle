package org.roag.rest;

import com.google.gson.Gson;
import org.roag.ds.OperationResult;
import org.roag.ds.SubscriberRepository;
import org.roag.model.Rss;
import org.roag.model.RssStatus;
import org.roag.model.Subscriber;
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
@Path("profile")
public class ProfileManager
{
    final private Logger logger = LoggerFactory.getLogger(ProfileManager.class);

    @Context
    private Request request;

    @Autowired
    private SubscriberRepository subscriberRepository;

    private Gson gson = new Gson();

    @GET
    @Path("/{id}/subscriptions")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllSubscriptions(@PathParam("id") String id)
    {
        logger.debug("Fetch all subscriptions for {}", id);
        try
        {
            Subscriber subscriber = subscriberRepository.getSubscriber(id);
            String result=gson.toJson(subscriber.getRsslist());
            return Response.ok(result, MediaType.APPLICATION_JSON_TYPE).build();
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @POST
    @Path("/{id}/subscribe")
    @Consumes("application/x-www-form-urlencoded")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addSubscription(@PathParam("id") String id,
                                    @FormParam("rss") String rss)
    {
        logger.debug("Add new subscription {} for {}", rss, id);
        try
        {
            Subscriber subscriber = subscriberRepository.getSubscriber(id);
            for (Rss r : subscriber.getRsslist())
                if (r.getRss().equals(rss))
                    return Response.status(Response.Status.CONFLICT).build();

            Rss _rss = new Rss();
            _rss.setRss(rss);
            _rss.setStatus(RssStatus.ACTIVE.toString());
            subscriber.getRsslist().add(_rss);
            OperationResult result = subscriberRepository.updateSubscriber(subscriber);
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
    @Path("/{id}/unsubscribe")
    @Consumes("application/x-www-form-urlencoded")
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeSubscription(@PathParam("id") String id,
                                       @FormParam("rss") String rss)
    {
        logger.debug("Remove subscription {} for {}", rss, id);
        try
        {
            Subscriber subscriber = subscriberRepository.getSubscriber(id);
            List<Rss> rssList=subscriber.getRsslist();
            for (int i=0; i< rssList.size(); i++) {
                Rss _rss=rssList.get(i);
                if (_rss.getRss().equals(rss))
                    rssList.remove(i);
            }
            subscriber.setRsslist(rssList);

            OperationResult result = subscriberRepository.updateSubscriber(subscriber);
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
