package org.roag.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * Created by eurohlam on 19/02/2018.
 */
@Component
@Scope("prototype")
public class ProfileRestClient implements RestClient {

    private final Logger logger = LoggerFactory.getLogger(ProfileRestClient.class);

    private static final String PROFILE_PATH = "profile/";
    private static final String SERVICE_PATH = "service/";

    private String restHost;
    private String restPort;
    private String restPath;

    private WebTarget target;


    @Autowired
    public ProfileRestClient(@Autowired ClientHelper clientHelper) {
        this.restPath = clientHelper.getRestPath();
        this.restPort = clientHelper.getRestPort();
        this.restHost = clientHelper.getRestHost();
        target = clientHelper.getWebTarget();
    }

    private Response sendRequest(String path, RequestMethod method, String json) {
        Response response = sendRequest(target, path, method, json);

        if (response == null) {
            logger.error("Response from RESt service is null. Probably, unsupported method has been called");
        } else if (response.getStatus() != 200) {
            logger.error("Failure during interaction with REST service. Code: {}; errorMessage: {}", response.getStatus(),
                    Response.Status.fromStatusCode(response.getStatus()).getReasonPhrase());
        }
        return response;
    }

    public Response getUserData(String username) {
        logger.debug("Trying to get data for user {} via REST service {}:{}{}",
                username, restHost, restPort, restPath);
        return sendRequest(PROFILE_PATH + username, GET, null);
    }

    public Response getSubscriber(String username, String subscriberId) {
        logger.debug("Trying to get data for subscriber {} of user {} via REST service {}:{}{}",
                subscriberId, username, restHost, restPort, restPath);
        return sendRequest(PROFILE_PATH + username + "/" + subscriberId, GET, null);
    }

    public Response resumeSubscriber(String username, String subscriberId) {
        logger.debug("Trying to resume subscriber {} of user {} via REST service {}:{}{}",
                subscriberId, username, restHost, restPort, restPath);
        return sendRequest(PROFILE_PATH + username + "/" + subscriberId + "/resume", GET, null);
    }

    public Response suspendSubscriber(String username, String subscriberId) {
        logger.debug("Trying to suspend subscriber {} of user {} via REST service {}:{}{}",
                subscriberId, username, restHost, restPort, restPath);
        return sendRequest(PROFILE_PATH + username + "/" + subscriberId + "/suspend", GET, null);
    }

    public Response removeSubscriber(String username, String subscriberId) {
        logger.debug("Trying to remove subscriber {} of user {} via REST service {}:{}{}",
                subscriberId, username, restHost, restPort, restPath);
        return sendRequest(PROFILE_PATH + username + "/" + subscriberId + "/remove", DELETE, null);
    }

    public Response updateSubscriber(String username, String json) {
        logger.debug("Trying to update subscriber of user {} via REST service {}:{}{}",
                username, restHost, restPort, restPath);
        return sendRequest(PROFILE_PATH + username + "/update", PUT, json);
    }

    public Response addSubscriber(String username, String json) {
        logger.debug("Trying to add new subscriber for user {} via REST service {}:{}{}",
                username, restHost, restPort, restPath);
        return sendRequest(PROFILE_PATH + username + "/new", PUT, json);
    }

    public Response runPolling(String username) {
        logger.debug("Trying to run polling subscription for user {} via REST service {}:{}{}",
                username, restHost, restPort, restPath);
        return sendRequest(SERVICE_PATH + username, GET, null);
    }

}
