package org.roag.rest;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTestNg;
import org.testng.annotations.Test;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.junit.Assert.*;

/**
 * Created by eurohlam on 30.08.17.
 */
public class ServiceManagerTest extends JerseyTestNg.ContainerPerClassTest
{

    private final static String PATH = "service/";
    
    private String username ="test";
    private String email="test@mail.com";

    @Override
    protected Application configure()
    {
        return new ResourceConfig(ServiceManager.class);
    }

//    @Test(groups = {"Polling:GET"})
    public void runRssPollingForSubscriber()
    {
        final Response response = target(PATH + username + "/" + email).request().accept(MediaType.APPLICATION_JSON_TYPE).get();
        assertEquals("Polling RSS for subscriber " + email + " failed", 200, response.getStatus());
    }

    @Test(groups = {"Polling:GET"})
    public void runRssPollingForUser()
    {
        final Response response = target(PATH + username).request().accept(MediaType.APPLICATION_JSON_TYPE).get();
        assertEquals("Polling RSS for user " + username + " failed", 200, response.getStatus());
    }

}
