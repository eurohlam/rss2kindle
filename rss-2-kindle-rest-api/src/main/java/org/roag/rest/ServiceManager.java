package org.roag.rest;

import org.roag.camel.Rss2XmlHandler;
import org.roag.ds.SubscriberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by eurohlam on 28/11/2017.
 */
@Service
@Path("service/{username: [a-zA-Z][a-zA-Z_0-9]*}")
public class ServiceManager {

    final private Logger logger = LoggerFactory.getLogger(ServiceManager.class);

    @Autowired
    private Rss2XmlHandler rss2XmlHandler;
    @Autowired
    private SubscriberRepository subscriberRepository;

    @GET
    @Path("/{email: \\w+@\\w+\\.[a-zA-Z]{2,}}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response runRssPollingForSubscriber(@PathParam("username") String username, @PathParam("email") String id)
    {
        logger.info("Run RSS polling by demand: user {}, subscriber {}", username, id);
        try
        {
            rss2XmlHandler.runRssPollingForSubscriber(username, id);
            return Response.ok("{ \"status\" : \"Polled all subscriptions for subscriber "+ id + "\" }", MediaType.APPLICATION_JSON_TYPE).build();
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response runRssPollingForUser(@PathParam("username") String username)
    {
        logger.info("Run RSS polling by demand: user {}", username);
        try
        {
            rss2XmlHandler.runRssPollingForList(username, subscriberRepository.findAllSubscribersByUser(username));
            return Response.ok("{ \"status\" : \"Polled all subscriptions\" }", MediaType.APPLICATION_JSON_TYPE).build();
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}
