package org.roag.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

/**
 * Created by eurohlam on 19/02/2018.
 */
@Component
@Scope("prototype")
public class RestClient {

    private static final Logger logger = LoggerFactory.getLogger(RestClient.class);

    private static final String PROFILE_PATH = "profile/";
    private static final String USERS_PATH = "users/";
    private static final String SERVICE_PATH = "service/";
    private static final String SEND_EMAIL_PATH = "email/send";

    private String restHost;
    private String restPort;
    private String restPath;

    private Client client;
    private WebTarget target;

    private enum METHOD {
        GET,
        PUT,
        POST,
        DELETE,
        PATCH
    }

    @Autowired
    public RestClient(@Value("${rest.host}") String restHost, @Value("${rest.port}") String restPort, @Value("${rest.path}") String restPath) {
        this.restPath = restPath;
        this.restPort = restPort;
        this.restHost = restHost;
        client = ClientBuilder.newClient();
        target = client.target(restHost + ":" + restPort + restPath);
    }

    private Response sendRequest(String path, METHOD method, String json) {
        Response response = null;
        if (method == METHOD.GET)
            response = target.path(path).request().get(Response.class);
        else if (method == METHOD.PUT)
            response = target.path(path).request().put(Entity.json(json), Response.class);
        else if (method == METHOD.DELETE)
            response = target.path(path).request().delete(Response.class);
        else if (method == METHOD.POST)
            response = target.path(path).request().post(Entity.json(json), Response.class);

        if (response == null)
            logger.error("Response from RESt service is null. Probably, unsupported method has been called");
        else if (response.getStatus() != 200)
            logger.error("Failure during interaction with REST service. Code: {}; errorMessage: {}", response.getStatus(),
                    Response.Status.fromStatusCode(response.getStatus()).getReasonPhrase());
        return response;
    }

    public Response getUserData(String username) {
        logger.debug("Trying to get data for user {} via REST service {}:{}{}", username, restHost, restPort, restPath);
        return sendRequest(PROFILE_PATH + username, METHOD.GET, null);
    }

    public Response resumeSubscriber(String username, String subscriberId) {
        logger.debug("Trying to resume user {} via REST service {}:{}{}", username, restHost, restPort, restPath);
        return sendRequest(PROFILE_PATH + username + "/" + subscriberId + "/resume", METHOD.GET, null);
    }

    public Response suspendSubscriber(String username, String subscriberId) {
        logger.debug("Trying to suspend user {} via REST service {}:{}{}", username, restHost, restPort, restPath);
        return sendRequest(PROFILE_PATH + username + "/" + subscriberId + "/suspend", METHOD.GET, null);
    }

    public Response removeSubscriber(String username, String subscriberId) {
        logger.debug("Trying to remove user {} via REST service {}:{}{}", username, restHost, restPort, restPath);
        return sendRequest(PROFILE_PATH + username + "/" + subscriberId + "/remove", METHOD.DELETE, null);
    }

    public Response updateSubscriber(String username, String json) {
        logger.debug("Trying to update user {} via REST service {}:{}{}", username, restHost, restPort, restPath);
        return sendRequest(PROFILE_PATH + username + "/update", METHOD.PUT, json);
    }

    public Response addSubscriber(String username, String json) {
        logger.debug("Trying to add new subscriber for user {} via REST service {}:{}{}", username, restHost, restPort, restPath);
        return sendRequest(PROFILE_PATH + username + "/new", METHOD.PUT, json);
    }

    public Response runPolling(String username) {
        logger.debug("Trying to run polling subscription for user {} via REST service {}:{}{}", username, restHost, restPort, restPath);
        return sendRequest(SERVICE_PATH + username, METHOD.GET, null);
    }

    public Response addUser(String json) {
        logger.debug("Trying to add new user via REST service {}:{}{}", restHost, restPort, restPath);
        return sendRequest(USERS_PATH + "new", METHOD.POST, json);
    }

    public Response getUser(String username) {
        logger.debug("Trying to get user {} via REST service {}:{}{}", username, restHost, restPort, restPath);
        return sendRequest(USERS_PATH + username, METHOD.GET, null);
    }

    public Response updateUser(String json) {
        logger.debug("Trying to update user via REST service {}:{}{}", restHost, restPort, restPath);
        return sendRequest(USERS_PATH + "update", METHOD.PUT, json);
    }

    public Response removeUser(String username) {
        logger.debug("Trying to remove user {} via REST service {}:{}{}", username, restHost, restPort, restPath);
        return sendRequest(USERS_PATH + username + "/remove", METHOD.DELETE, null);
    }

    public Response sendEmailToUser(String username, String subject, String message) {
        logger.debug("Sending email to user {} with subject {}", username, subject);
        return target.path(SEND_EMAIL_PATH + "/" + username).
                queryParam("subject", subject).
                queryParam("message", message).
                request().get(Response.class);
    }

    public Response sendEmailToAny(String to, String subject, String from, String fromPersonal, String message) {
        logger.debug("Sending email to {} from {} with subject {}", to, from, subject);
        return target.path(SEND_EMAIL_PATH).
                queryParam("to", to).
                queryParam("from", from).
                queryParam("fromPersonal", fromPersonal).
                queryParam("subject", subject).
                queryParam("message", message).
                request().get(Response.class);
    }
}
