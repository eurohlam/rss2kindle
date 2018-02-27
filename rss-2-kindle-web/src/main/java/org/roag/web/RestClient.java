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
public class RestClient
{

    final private Logger logger = LoggerFactory.getLogger(RestClient.class);

    private String restHost;
    private String restPort;
    private String restPath;

    private Client restClient;
    private WebTarget target;

    @Autowired
    public RestClient(@Value("${rest.host}") String restHost, @Value("${rest.port}") String restPort, @Value("${rest.path}") String restPath)
    {
        this.restPath = restPath;
        this.restPort = restPort;
        this.restHost = restHost;
        restClient = ClientBuilder.newClient();
        target = restClient.target(restHost + ":" + restPort + restPath);
    }

    private String sendGETRequest(String path)
    {
        Response response = target.path(path).request().get();
        if (response.getStatus() == 200) {
            return response.readEntity(String.class);
        } else
            return "ERROR: " + Response.Status.fromStatusCode(response.getStatus()).getReasonPhrase();
    }

    public String getUserData(String username)
    {
        logger.debug("Trying to get data for user {} via REST service {}:{}{}", username, restHost, restPort, restPath);
        return sendGETRequest("profile/" + username);
    }

    public String resumeSubscriber(String username, String subscriberId)
    {
        logger.debug("Trying to resume user {} via REST service {}:{}{}", username, restHost, restPort, restPath);
        return sendGETRequest("profile/" + username + "/" + subscriberId + "/resume");
    }

    public String suspendSubscriber(String username, String subscriberId)
    {
        logger.debug("Trying to suspend user {} via REST service {}:{}{}", username, restHost, restPort, restPath);
        return sendGETRequest("profile/" + username + "/" + subscriberId + "/suspend");
    }

    public String removeSubscriber(String username, String subscriberId)
    {
        logger.debug("Trying to remove user {} via REST service {}:{}{}", username, restHost, restPort, restPath);
        Response response = target.path("profile/" + username + "/" + subscriberId + "/remove").request().delete();
        if (response.getStatus() == 200) {
            return response.readEntity(String.class);
        } else
            return "ERROR: " + Response.Status.fromStatusCode(response.getStatus()).getReasonPhrase();
    }

    public String updateSubscriber(String username, String message)
    {
        logger.debug("Trying to update user {} via REST service {}:{}{}", username, restHost, restPort, restPath);
        Response response = target.path("profile/" + username + "/update").request().put(Entity.json(message));
        if (response.getStatus() == 200) {
            return response.readEntity(String.class);
        } else
            return "ERROR: " + Response.Status.fromStatusCode(response.getStatus()).getReasonPhrase();
    }

/*
    public String addSubscriber(String username, String email, String name, String rss)
    {
        Response response = target.request().put(Entity.json(factory.convertPojo2Json(factory.newSubscriber(new_email, new_name, new_rss))), Response.class);
        if (response.getStatus() == 200) {
            logger.debug("Received data for user {} from REST service {}:{}{}", restHost, restPort, restPath);
            return response.readEntity(String.class);
        } else
            return "ERROR: " + Response.Status.fromStatusCode(response.getStatus()).getReasonPhrase();

    }
*/

}
