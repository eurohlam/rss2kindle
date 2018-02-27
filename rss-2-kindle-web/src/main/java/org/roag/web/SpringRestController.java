package org.roag.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.core.MediaType;

/**
 * Created by eurohlam on 19/02/2018.
 */
@RestController
@RequestMapping("rest")
@Secured({"ROLE_USER", "ROLE_ADMIN"})
public class SpringRestController
{
    @Autowired
    private RestClient client;

    @RequestMapping(value = "/{username}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public String getUserData(@PathVariable("username") String username)
    {
        return client.getUserData(username);
    }

    @RequestMapping(value = "/{username}/{subscriber:\\w+@\\w+\\.[a-zA-Z]{2,}}/suspend", method = RequestMethod.GET)
    public String suspendSubscriber(@PathVariable("username") String username, @PathVariable("subscriber") String subscriber)
    {
        return client.suspendSubscriber(username, subscriber);
    }

    @RequestMapping(value = "/{username}/{subscriber:\\w+@\\w+\\.[a-zA-Z]{2,}}/resume", method = RequestMethod.GET)
    public String resumeSubscriber(@PathVariable("username") String username, @PathVariable("subscriber") String subscriber)
    {
        return client.resumeSubscriber(username, subscriber);
    }

    //TODO: resolve issue with CSRF for ajax https://docs.spring.io/spring-security/site/docs/3.2.0.CI-SNAPSHOT/reference/html/csrf.html#csrf-include-csrf-token-ajax
    @RequestMapping(value = "/{username}/{subscriber:\\w+@\\w+\\.[a-zA-Z]{2,}}/remove", method = RequestMethod.DELETE)
    public String removeSubscriber(@PathVariable("username") String username, @PathVariable("subscriber") String subscriber)
    {
        return client.removeSubscriber(username, subscriber);
    }

    //TODO: resolve issue with CSRF for ajax https://docs.spring.io/spring-security/site/docs/3.2.0.CI-SNAPSHOT/reference/html/csrf.html#csrf-include-csrf-token-ajax
    @RequestMapping(value = "/{username}/update", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON)
    public String updateSubscriber(@PathVariable("username") String username, @RequestBody String message)
    {
        return client.updateSubscriber(username, message);
    }

}
