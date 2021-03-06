package org.roag.rest;

import org.jvnet.hk2.annotations.Service;
import org.roag.camel.SmtpSender;
import org.roag.ds.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by eurohlam on 01.06.18.
 */
@Service
@Path("email")
public class EmailService {

    private final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SmtpSender smtpSender;

    @GET
    @Path("/send/{username: [a-zA-Z][a-zA-Z_0-9]*}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response sendEmailToUser(@PathParam("username") String username, @QueryParam("subject") String subject,
                                    @QueryParam("message") String message) {
        try {
            String email = userRepository.getUser(username).getEmail();
            smtpSender.send(email, subject, message);
            return Response.ok("{ \"status\" : \"Email has been sent to " + email + "\" }",
                    MediaType.APPLICATION_JSON_TYPE).build();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @GET
    @Path("/send")
    @Produces(MediaType.APPLICATION_JSON)
    public Response sendEmailToAny(@QueryParam("to") String to, @QueryParam("subject") String subject,
                                   @QueryParam("from") String from, @QueryParam("fromPersonal") String fromPersonal,
                                   @QueryParam("message") String message) {
        logger.info("Sending email to {}", to);
        try {
            smtpSender.send(to, subject, from, fromPersonal, message);
            return Response.ok("{ \"status\" : \"Email has been sent to " + to + " from " + from + "\" }",
                    MediaType.APPLICATION_JSON_TYPE).build();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

}
