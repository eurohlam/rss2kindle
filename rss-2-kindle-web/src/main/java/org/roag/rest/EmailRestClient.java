package org.roag.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

/**
 * Created by eurohlam on 22/03/2019.
 */
@Component
public class EmailRestClient implements RestClient {

    private final Logger logger = LoggerFactory.getLogger(EmailRestClient.class);

    private static final String SEND_EMAIL_PATH = "email/send/";

    private WebTarget target;

    public EmailRestClient(@Value("${rest.host}") String restHost, @Value("${rest.port}") String restPort,
                           @Value("${rest.path}") String restPath) {
        target = ClientBuilder.newClient().target(restHost + ":" + restPort + restPath + "/" + SEND_EMAIL_PATH);
    }

    public Response sendEmailToUser(String username, String subject, String message) {
        logger.debug("Sending email to user {} with subject {}", username, subject);
        return target
                .path(username)
                .queryParam("subject", subject)
                .queryParam("message", message)
                .request().get(Response.class);
    }

    public Response sendEmailToAny(String to, String subject, String from, String fromPersonal, String message) {
        logger.debug("Sending email to {} from {} with subject {}", to, from, subject);
        return target
                .queryParam("to", to)
                .queryParam("from", from)
                .queryParam("fromPersonal", fromPersonal)
                .queryParam("subject", subject)
                .queryParam("message", message)
                .request()
                .get(Response.class);
    }
}
