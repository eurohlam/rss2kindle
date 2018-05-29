package org.roag.rest;

import org.roag.camel.Rss2XmlHandler;
import org.roag.camel.SMTPSender;
import org.roag.ds.SubscriberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by eurohlam on 28/11/2017.
 */
@Service
@Path("service/{username: [a-zA-Z][a-zA-Z_0-9]*}")
public class ServiceManager {

    private static final Logger logger = LoggerFactory.getLogger(ServiceManager.class);

    @Autowired
    private Rss2XmlHandler rss2XmlHandler;
    @Autowired
    private SubscriberRepository subscriberRepository;
    @Autowired
    private SMTPSender smtpSender;

    @GET
    @Path("/{email: \\w+@\\w+\\.[a-zA-Z]{2,}}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response runRssPollingForSubscriber(@PathParam("username") String username, @PathParam("email") String id) {
        logger.info("Run RSS polling by demand: user {}, subscriber {}", username, id);
        try {
            rss2XmlHandler.runRssPollingForSubscriber(username, id);
            return Response.ok("{ \"status\" : \"Polled all subscriptions for subscriber " + id + "\" }", MediaType.APPLICATION_JSON_TYPE).build();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response runRssPollingForUser(@PathParam("username") String username) {
        logger.info("Run RSS polling by demand: user {}", username);
        try {
            rss2XmlHandler.runRssPollingForList(username, subscriberRepository.findAllSubscribersByUser(username));
            return Response.ok("{ \"status\" : \"Polled all subscriptions\" }", MediaType.APPLICATION_JSON_TYPE).build();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @GET
    @Path("/send")
    @Produces(MediaType.APPLICATION_JSON)
    public Response sendEmailToUser(@PathParam("username") String username, @QueryParam("subject") String subject, @QueryParam("message") String message) {
        logger.info("Sending email to {}", username);
        try {
            String email = subscriberRepository.getUserRepository().getUser(username).getEmail();
            smtpSender.send(email, subject, message);
            return Response.ok("{ \"status\" : \"Email has been sent to "+email + "\" }", MediaType.APPLICATION_JSON_TYPE).build();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}
