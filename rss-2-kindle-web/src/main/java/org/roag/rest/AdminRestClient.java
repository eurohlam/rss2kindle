package org.roag.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import static org.springframework.web.bind.annotation.RequestMethod.*;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created by eurohlam on 22/03/2019.
 */
@Component
@Scope("prototype")
public class AdminRestClient implements RestClient {

    private final Logger logger = LoggerFactory.getLogger(AdminRestClient.class);

    private static final String USERS_PATH = "users/";

    private String restHost;
    private String restPort;
    private String restPath;

    private Client client;
    private WebTarget target;


    @Autowired
    public AdminRestClient(@Value("${rest.host}") String restHost, @Value("${rest.port}") String restPort, @Value("${rest.path}") String restPath) {
        this.restPath = restPath;
        this.restPort = restPort;
        this.restHost = restHost;
        client = ClientBuilder.newClient();
        target = client.target(restHost + ":" + restPort + restPath);
    }

    private Response sendRequest(String path, RequestMethod method, String json) {
        Response response =sendRequest(target, path, method, json);

        if (response == null)
            logger.error("Response from RESt service is null. Probably, unsupported method has been called");
        else if (response.getStatus() != 200)
            logger.error("Failure during interaction with REST service. Code: {}; errorMessage: {}", response.getStatus(),
                    Response.Status.fromStatusCode(response.getStatus()).getReasonPhrase());
        return response;
    }


    public Response addUser(String json) {
        logger.debug("Trying to add new user via REST service {}:{}{}", restHost, restPort, restPath);
        return sendRequest(USERS_PATH + "new", POST, json);
    }

    public Response getUser(String username) {
        logger.debug("Trying to get user {} via REST service {}:{}{}", username, restHost, restPort, restPath);
        return sendRequest(USERS_PATH + username, GET, null);
    }

    public Response updateUser(String json) {
        logger.debug("Trying to update user via REST service {}:{}{}", restHost, restPort, restPath);
        return sendRequest(USERS_PATH + "update", PUT, json);
    }

    public Response removeUser(String username) {
        logger.debug("Trying to remove user {} via REST service {}:{}{}", username, restHost, restPort, restPath);
        return sendRequest(USERS_PATH + username + "/remove", DELETE, null);
    }

    public Response lockUser(String username) {
        logger.debug("Trying to lock user {} via REST service {}:{}{}", username, restHost, restPort, restPath);
        return sendRequest(USERS_PATH + username + "/lock", GET, null);
    }

    public Response unlockUser(String username) {
        logger.debug("Trying to unlock user {} via REST service {}:{}{}", username, restHost, restPort, restPath);
        return sendRequest(USERS_PATH + username + "/unlock", GET, null);
    }

    public Response getAllUsers() {
        logger.debug("Trying to get all users via REST service {}:{}{}", restHost, restPort, restPath);
        return sendRequest(USERS_PATH, GET, null);
    }
}
