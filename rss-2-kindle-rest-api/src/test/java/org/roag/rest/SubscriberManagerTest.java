package org.roag.rest;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTestNg;
import org.testng.annotations.Test;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

/**
 * Created by eurohlam on 05.09.17.
 */
public class SubscriberManagerTest extends JerseyTestNg.ContainerPerClassTest
{
    @Override
    protected Application configure()
    {
        return new ResourceConfig(SubscriberManager.class);
    }

    @Test(groups = {"GET"})
    public void getAllSubscribersTest()
    {
        final Response response = target("subscribers").request().accept(MediaType.APPLICATION_JSON_TYPE).get();

        assertEquals(200, response.getStatus());
    }

    @Test(groups = {"GET"})
    public void getSubscriberOperationsTest()
    {
        Response response = target("subscribers/test@mail.com").request().accept(MediaType.APPLICATION_JSON_TYPE).get();
        assertEquals(200, response.getStatus());

        response = target("subscribers/test@mail.com/suspend").request().accept(MediaType.APPLICATION_JSON_TYPE).get();
        assertEquals(200, response.getStatus());

        response = target("subscribers/test@mail.com/resume").request().accept(MediaType.APPLICATION_JSON_TYPE).get();
        assertEquals(200, response.getStatus());
    }

    @Test(groups = {"CRUD"})
    public void crudTest()
    {
        //TODO
        final Response response = target("subscribers/test@mail.com").request().accept(MediaType.APPLICATION_JSON_TYPE).get();

        assertEquals(200, response.getStatus());
    }

}
