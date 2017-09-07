package org.roag.rest;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTestNg;
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
public class ProfileManagerTest extends JerseyTestNg.ContainerPerClassTest
{

    private String id="test@mail.com";

    @Override
    protected Application configure()
    {
        return new ResourceConfig(ProfileManager.class);
    }

    @Test(groups = {"ProfileManager:GET"})
    public void getAllSubscriptionsTest()
    {
        final Response response = target("profile/" + id + "/subscriptions").request().accept(MediaType.APPLICATION_JSON_TYPE).get();

        assertEquals("Getting all subscriptions failed", 200, response.getStatus());
    }

    @Test(groups = {"ProfileManager:POST"})
    public void subscriptionOperationsTest()
    {
        String subscription="http://new_test.com/rss";
        //subscribe
        Form form = new Form();
        form.param("rss", subscription);
        Response response = target("profile/" + id + "/subscribe").request().post(Entity.form(form), Response.class);
        assertEquals("Adding new subscription failed", 200, response.getStatus());

        //unsubscribe
        Form form_update = new Form();
        form_update.param("rss", subscription);
        response = target("profile/" + id + "/unsubscribe").request().post(Entity.form(form_update), Response.class);
        assertEquals("Deleting subscription failed", 200, response.getStatus());
    }

}
