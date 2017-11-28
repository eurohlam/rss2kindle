package org.roag.rest;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTestNg;
import org.roag.model.Subscriber;
import org.roag.model.User;
import org.roag.service.SubscriberFactory;
import org.testng.annotations.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Form;
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

    @Test(groups = {"Polling:GET"})
    public void runRssPollingForSubscriber()
    {
        final Response response = target(PATH + username + "/" + email).request().accept(MediaType.APPLICATION_JSON_TYPE).get();
        assertEquals("Polling RSS subscribers failed", 200, response.getStatus());
    }

    @Test(groups = {"Subscribers:GET"})
    public void getSubscriberOperationsTest()
    {
        Response response = target(PATH + username + "/" +email).request().accept(MediaType.APPLICATION_JSON_TYPE).get();
        assertEquals("Getting subscriber failed", 200, response.getStatus());

        response = target(PATH + username + "/" +email + "/suspend").request().accept(MediaType.APPLICATION_JSON_TYPE).get();
        assertEquals("Suspending subscriber failed", 200, response.getStatus());

        response = target(PATH + username + "/" +email + "/resume").request().accept(MediaType.APPLICATION_JSON_TYPE).get();
        assertEquals("Resuming subscriber failed", 200, response.getStatus());
    }


    @Test(groups = {"Subscriptions:POST"})
    public void subscriptionOperationsTest()
    {
        String subscription="http://new_test.com/rss";
        //subscribe
        Form form = new Form();
        form.param("rss", subscription);
        Response response = target(PATH + username + "/"+ email + "/subscribe").request().post(Entity.form(form), Response.class);
        assertEquals("Adding new subscription failed", 200, response.getStatus());

        //unsubscribe
        Form form_update = new Form();
        form_update.param("rss", subscription);
        response = target(PATH + username + "/" + email + "/unsubscribe").request().post(Entity.form(form_update), Response.class);
        assertEquals("Deleting subscription failed", 200, response.getStatus());
    }

}
