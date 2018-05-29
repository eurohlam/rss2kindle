package org.roag.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by eurohlam on 19/02/2018.
 */
@RestController
@RequestMapping("rest")
@Secured({"ROLE_USER", "ROLE_ADMIN"})
public class SpringRestController {
    @Autowired
    private RestClient client;

    private static final String ACCESS_DENIED_MESSAGE = "{ \"status\" : \"Access denied\" }";

    @RequestMapping(value = "/profile/{username}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public String getUserData(@PathVariable("username") String username) {
        return isAccessAllowed(username) ? client.getUserData(username).readEntity(String.class) : ACCESS_DENIED_MESSAGE;
    }

    @RequestMapping(value = "/profile/{username}/{subscriber:\\w+@\\w+\\.[a-zA-Z]{2,}}/suspend", method = RequestMethod.GET)
    public String suspendSubscriber(@PathVariable("username") String username, @PathVariable("subscriber") String subscriber) {
        return isAccessAllowed(username) ? client.suspendSubscriber(username, subscriber).readEntity(String.class) : ACCESS_DENIED_MESSAGE;
    }

    @RequestMapping(value = "/profile/{username}/{subscriber:\\w+@\\w+\\.[a-zA-Z]{2,}}/resume", method = RequestMethod.GET)
    public String resumeSubscriber(@PathVariable("username") String username, @PathVariable("subscriber") String subscriber) {
        return isAccessAllowed(username) ? client.resumeSubscriber(username, subscriber).readEntity(String.class) : ACCESS_DENIED_MESSAGE;
    }

    @RequestMapping(value = "/profile/{username}/{subscriber:\\w+@\\w+\\.[a-zA-Z]{2,}}/remove", method = RequestMethod.DELETE)
    public String removeSubscriber(@PathVariable("username") String username, @PathVariable("subscriber") String subscriber) {
        return isAccessAllowed(username) ? client.removeSubscriber(username, subscriber).readEntity(String.class) : ACCESS_DENIED_MESSAGE;
    }

    @RequestMapping(value = "/profile/{username}/update", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON)
    public String updateSubscriber(@PathVariable("username") String username, @RequestBody String message) {
        return isAccessAllowed(username) ? client.updateSubscriber(username, message).readEntity(String.class) : ACCESS_DENIED_MESSAGE;
    }

    @RequestMapping(value = "/profile/{username}/new", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON)
    public Response newSubscriber(@PathVariable("username") String username, @RequestBody String message) {
        return isAccessAllowed(username) ? client.addSubscriber(username, message) : Response.status(Response.Status.UNAUTHORIZED).build();//TODO: error handling does not work properly
    }

    @RequestMapping(value = "/service/{username}", method = RequestMethod.GET)
    public String runPolling(@PathVariable("username") String username) {
        return isAccessAllowed(username) ? client.runPolling(username).readEntity(String.class) : ACCESS_DENIED_MESSAGE;
    }

    private boolean isAccessAllowed(String username) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserDetails) {
            UserDetails ud = (UserDetails) auth.getPrincipal();
            if (ud.getUsername().equals(username))
                return true;
        }
        return false;
    }

}
