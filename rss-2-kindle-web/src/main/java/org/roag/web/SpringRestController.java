package org.roag.web;

import org.roag.rest.AdminRestClient;
import org.roag.rest.ProfileRestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.core.MediaType;

/**
 * Created by eurohlam on 19/02/2018.
 */
@RestController
@RequestMapping("rest")
public class SpringRestController {

    @Autowired
    private ProfileRestClient profileRestClient;
    @Autowired
    private AdminRestClient adminRestClient;

    private static final String ACCESS_DENIED_MESSAGE = "{ \"status\" : \"Access denied\" }";

    @RequestMapping(value = "/profile/{username}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public String getUserData(@PathVariable("username") String username) {
        return isAccessAllowed(username) ? profileRestClient.getUserData(username).readEntity(String.class) : ACCESS_DENIED_MESSAGE;
    }

    @RequestMapping(value = "/profile/{username}/{subscriber:\\w+@\\w+\\.[a-zA-Z]{2,}}", method = RequestMethod.GET)
    public String getSubscriber(@PathVariable("username") String username, @PathVariable("subscriber") String subscriber) {
        return isAccessAllowed(username) ? profileRestClient.getSubscriber(username, subscriber).readEntity(String.class) : ACCESS_DENIED_MESSAGE;
    }

    @RequestMapping(value = "/profile/{username}/{subscriber:\\w+@\\w+\\.[a-zA-Z]{2,}}/suspend", method = RequestMethod.GET)
    public String suspendSubscriber(@PathVariable("username") String username, @PathVariable("subscriber") String subscriber) {
        return isAccessAllowed(username) ? profileRestClient.suspendSubscriber(username, subscriber).readEntity(String.class) : ACCESS_DENIED_MESSAGE;
    }

    @RequestMapping(value = "/profile/{username}/{subscriber:\\w+@\\w+\\.[a-zA-Z]{2,}}/resume", method = RequestMethod.GET)
    public String resumeSubscriber(@PathVariable("username") String username, @PathVariable("subscriber") String subscriber) {
        return isAccessAllowed(username) ? profileRestClient.resumeSubscriber(username, subscriber).readEntity(String.class) : ACCESS_DENIED_MESSAGE;
    }

    @RequestMapping(value = "/profile/{username}/{subscriber:\\w+@\\w+\\.[a-zA-Z]{2,}}/remove", method = RequestMethod.DELETE)
    public String removeSubscriber(@PathVariable("username") String username, @PathVariable("subscriber") String subscriber) {
        return isAccessAllowed(username) ? profileRestClient.removeSubscriber(username, subscriber).readEntity(String.class) : ACCESS_DENIED_MESSAGE;
    }

    @RequestMapping(value = "/profile/{username}/update", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON)
    public String updateSubscriber(@PathVariable("username") String username, @RequestBody String message) {
        return isAccessAllowed(username) ? profileRestClient.updateSubscriber(username, message).readEntity(String.class) : ACCESS_DENIED_MESSAGE;
    }

    @RequestMapping(value = "/profile/{username}/new", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON)
    public String newSubscriber(@PathVariable("username") String username, @RequestBody String message) {
        return isAccessAllowed(username) ? profileRestClient.addSubscriber(username, message).readEntity(String.class) : ACCESS_DENIED_MESSAGE;
    }

    @RequestMapping(value = "/service/{username}", method = RequestMethod.GET)
    public String runPolling(@PathVariable("username") String username) {
        return isAccessAllowed(username) ? profileRestClient.runPolling(username).readEntity(String.class) : ACCESS_DENIED_MESSAGE;
    }

    @Secured("ROLE_ADMIN")
    @RequestMapping(value = "/admin/users", method = RequestMethod.GET)
    public String getAllUsers() {
        return adminRestClient.getAllUsers().readEntity(String.class);
    }

    @RequestMapping(value = "/admin/{username}/lock", method = RequestMethod.GET)
    public String lockUser(@PathVariable("username") String username) {
        return adminRestClient.lockUser(username).readEntity(String.class);
    }

    @RequestMapping(value = "/admin/{username}/unlock", method = RequestMethod.GET)
    public String unlockUser(@PathVariable("username") String username) {
        return adminRestClient.unlockUser(username).readEntity(String.class);
    }

    @RequestMapping(value = "/admin/{username}/remove", method = RequestMethod.DELETE)
    public String removeUser(@PathVariable("username") String username) {
        return adminRestClient.removeUser(username).readEntity(String.class);
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
