package org.roag.rest;

import org.roag.ds.OperationResult;
import org.roag.ds.SubscriberRepository;
import org.roag.ds.UserRepository;
import org.roag.model.Rss;
import org.roag.model.RssStatus;
import org.roag.model.Subscriber;
import org.roag.service.ModelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by eurohlam on 30.08.17.
 */
@Service
@Path("profile/{username: [a-zA-Z_.0-9]*}")
public class ProfileManager {

    private final Logger logger = LoggerFactory.getLogger(ProfileManager.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubscriberRepository subscriberRepository;

    private ModelFactory modelFactory = new ModelFactory();

    private List<String> validationErrors = Collections.EMPTY_LIST;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserDetails(@PathParam("username") String username) {
        logger.debug("Fetch user details for user {}", username);
        try {
            String subscribers = modelFactory.pojo2Json(userRepository.getUser(username));
            return Response.ok(subscribers, MediaType.APPLICATION_JSON_TYPE).build();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @GET
    @Path("/subscribers")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllSubscribers(@PathParam("username") String username) {
        logger.debug("Fetch all subscribers for user {}", username);
        try {
            String subscribers = modelFactory.pojo2Json(subscriberRepository.findAllSubscribersByUser(username));
            return Response.ok(subscribers, MediaType.APPLICATION_JSON_TYPE).build();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @GET
    @Path("/{email: [\\w\\.]+@[\\w\\.]+}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSubscriber(@PathParam("username") String username, @PathParam("email") String subscriberId) {
        logger.debug("Fetch subscriber {} for user {}", subscriberId, username);
        try {
            String subscriber = modelFactory.pojo2Json(subscriberRepository.getSubscriber(username, subscriberId));
            return Response.ok(subscriber, MediaType.APPLICATION_JSON_TYPE).build();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @GET
    @Path("/{email: [\\w\\.]+@[\\w\\.]+}/suspend")
    @Produces(MediaType.APPLICATION_JSON)
    public Response suspendSubscriber(@PathParam("username") String username, @PathParam("email") String subscriberId) {
        logger.warn("Suspend subscriber {} for user {}", subscriberId, username);
        try {
            OperationResult result = subscriberRepository.suspendSubscriber(username, subscriberId);
            if (result == OperationResult.SUCCESS) {
                return Response.ok(result.toJson(), MediaType.APPLICATION_JSON_TYPE).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GET
    @Path("/{email: [\\w\\.]+@[\\w\\.]+}/resume")
    @Produces(MediaType.APPLICATION_JSON)
    public Response resumeSubscriber(@PathParam("username") String username, @PathParam("email") String subscriberId) {
        logger.warn("Resume subscriber {} for user {}", subscriberId, username);
        try {
            OperationResult result = subscriberRepository.resumeSubscriber(username, subscriberId);
            if (result == OperationResult.SUCCESS) {
                return Response.ok(result.toJson(), MediaType.APPLICATION_JSON_TYPE).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @POST
    @Path("/new")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @Deprecated
    public Response addSubscriber(@PathParam("username") String username,
                                  @FormParam("email") String email,
                                  @FormParam("name") String name,
                                  @FormParam("rss") String rss) {
        logger.info("Add new subscriber {} for user {}", email, username);
        try {
            Subscriber subscriber = modelFactory.newSubscriber(email, name, rss);
            if (!isValid(subscriber)) {
                return Response.status(Response.Status.BAD_REQUEST.getStatusCode(), validationErrors.toString()).build();
            }
            OperationResult result = subscriberRepository.addSubscriber(username, subscriber);
            if (result == OperationResult.SUCCESS) {
                return Response.ok(result.toJson(), MediaType.APPLICATION_JSON_TYPE).build();
            } else {
                return Response.status(Response.Status.CONFLICT).build();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PUT
    @Path("/new")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addSubscriber(@PathParam("username") String username,
                                  String message) {
        logger.info("Requested to add a new subscriber for user {} with data {}", username, message);
        try {
            Subscriber subscriber = modelFactory.json2Pojo(Subscriber.class, message);
            if (!isValid(subscriber)) {
                return Response.status(Response.Status.BAD_REQUEST.getStatusCode(), validationErrors.toString()).build();
            }
            OperationResult result = subscriberRepository.addSubscriber(username, subscriber);
            if (result == OperationResult.SUCCESS) {
                return Response.ok(result.toJson(), MediaType.APPLICATION_JSON_TYPE).build();
            } else {
                return Response.status(Response.Status.CONFLICT).build();
            }
        } catch (IllegalArgumentException ie) {
            logger.error(ie.getMessage(), ie);
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @POST
    @Path("/update")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @Deprecated
    public Response updateSubscriber(@PathParam("username") String username,
                                     @FormParam("email") String email,
                                     @FormParam("name") String name,
                                     @FormParam("rss") String rss) {
        logger.warn("Update existing subscriber {} for user {}", email, username);
        try {
            Subscriber subscriber = modelFactory.newSubscriber(email, name, rss);
            if (!isValid(subscriber)) {
                return Response.status(Response.Status.BAD_REQUEST.getStatusCode(), validationErrors.toString()).build();
            }
            OperationResult result = subscriberRepository.updateSubscriber(username, subscriber);
            if (result == OperationResult.SUCCESS) {
                return Response.ok(result.toJson(), MediaType.APPLICATION_JSON_TYPE).build();
            } else {
                return Response.status(Response.Status.CONFLICT).build();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PUT
    @Path("/update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateSubscriber(@PathParam("username") String username, String message) {
        logger.warn("Requested to update existing subscriber for user {} with data {}", username, message);
        try {
            Subscriber subscriber = modelFactory.json2Pojo(Subscriber.class, message);
            if (!isValid(subscriber)) {
                return Response.status(Response.Status.BAD_REQUEST.getStatusCode(), validationErrors.toString()).build();
            }
            OperationResult result = subscriberRepository.updateSubscriber(username, subscriber);
            if (result == OperationResult.SUCCESS) {
                return Response.ok(result.toJson(), MediaType.APPLICATION_JSON_TYPE).build();
            } else {
                return Response.status(Response.Status.CONFLICT).build();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DELETE
    @Path("/{email: [\\w\\.]+@[\\w\\.]+}/remove")
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeSubscriber(@PathParam("username") String username, @PathParam("email") String subscriberId) {
        logger.warn("Remove subscriber {} for user {}", subscriberId, username);
        try {
            OperationResult result = subscriberRepository.removeSubscriber(username, subscriberId);
            if (result == OperationResult.SUCCESS) {
                return Response.ok(result.toJson(), MediaType.APPLICATION_JSON_TYPE).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GET
    @Path("/{email: [\\w\\.]+@[\\w\\.]+}/subscriptions")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllSubscriptions(@PathParam("username") String username, @PathParam("email") String subscriberId) {
        logger.debug("Fetch all subscriptions for subscriber {} by user {}", subscriberId, username);
        try {
            Subscriber subscriber = subscriberRepository.getSubscriber(username, subscriberId);
            String result = modelFactory.pojo2Json(subscriber.getRsslist());
            return Response.ok(result, MediaType.APPLICATION_JSON_TYPE).build();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @POST
    @Path("/{email: [\\w\\.]+@[\\w\\.]+}/subscribe")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addSubscription(@PathParam("username") String username,
                                    @PathParam("email") String subscriberId,
                                    @FormParam("rss") String rss) {
        logger.info("Add new subscription {} for subscriber {} by user {}", rss, subscriberId, username);
        try {
            Subscriber subscriber = subscriberRepository.getSubscriber(username, subscriberId);
            for (Rss r : subscriber.getRsslist()) {
                if (r.getRss().equals(rss)) {
                    return Response.status(Response.Status.CONFLICT.getStatusCode(), "Duplicated subscription").build();
                }
            }

            Rss rssObj = new Rss();
            rssObj.setRss(rss);
            rssObj.setStatus(RssStatus.ACTIVE.toString());
            subscriber.getRsslist().add(rssObj);
            OperationResult result = subscriberRepository.updateSubscriber(username, subscriber);
            if (result == OperationResult.SUCCESS) {
                return Response.ok(result.toJson(), MediaType.APPLICATION_JSON_TYPE).build();
            } else {
                return Response.status(Response.Status.CONFLICT).build();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @POST
    @Path("/{email: [\\w\\.]+@[\\w\\.]+}/unsubscribe")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeSubscription(@PathParam("username") String username,
                                       @PathParam("email") String subscriberId,
                                       @FormParam("rss") String rss) {
        logger.warn("Remove subscription {} for subscriber {} by user {}", rss, subscriberId, username);
        try {
            Subscriber subscriber = subscriberRepository.getSubscriber(username, subscriberId);
            List<Rss> rssList = subscriber.getRsslist();
            for (int i = 0; i < rssList.size(); i++) {
                Rss rssObj = rssList.get(i);
                if (rssObj.getRss().equals(rss)) {
                    rssList.remove(i);
                }
            }
            subscriber.setRsslist(rssList);

            OperationResult result = subscriberRepository.updateSubscriber(username, subscriber);
            if (result == OperationResult.SUCCESS) {
                return Response.ok(result.toJson(), MediaType.APPLICATION_JSON_TYPE).build();
            } else {
                return Response.status(Response.Status.CONFLICT).build();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    private boolean isValid(Subscriber subscriber) {
        List<String> errors = new ArrayList<>();
        boolean isValid = true;
        if (subscriber.getName().isEmpty()) {
            errors.add("subscriber's name must not be empty");
            isValid = false;
        }
        if (subscriber.getEmail().isEmpty()) {
            errors.add("subscriber's email must not be empty");
            isValid = false;
        }
        if (subscriber.getRsslist().size() < 1) {
            errors.add("subscriber must have at least 1 subscription");
            isValid = false;
        }
        if (!subscriber.getEmail().matches("[\\w.]+@[\\w.]+")) {
            errors.add("email contains unexpected symbols. It must satisfy the mask [\\w.]+@[\\w.]+");
            isValid = false;
        }
        for (Rss rss: subscriber.getRsslist()) {
            if (rss.getRss().isEmpty()) {
                errors.add("subscription must not be empty");
                isValid = false;
            }
            String pattern = "https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)";
            if (!rss.getRss().matches(pattern)) {
                errors.add("incorrect URL for subscription: " + rss.getRss());
                isValid = false;
            }
        }
        if (isValid) {
            validationErrors = Collections.EMPTY_LIST;
        } else {
            validationErrors = errors;
        }
        return isValid;
    }
}
