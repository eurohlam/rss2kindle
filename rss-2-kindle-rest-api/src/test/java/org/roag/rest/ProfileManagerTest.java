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
public class ProfileManagerTest extends JerseyTestNg.ContainerPerClassTest
{

    private String username ="test";
    private String email="test@mail.com";

    @Override
    protected Application configure()
    {
        return new ResourceConfig(ProfileManager.class);
    }

    @Test(groups = {"Subscribers:GET"})
    public void getAllSubscribersTest()
    {
        final Response response = target("profile/" + username).request().accept(MediaType.APPLICATION_JSON_TYPE).get();
        assertEquals("Getting all subscribers failed", 200, response.getStatus());
    }

    @Test(groups = {"Subscribers:GET"})
    public void getSubscriberOperationsTest()
    {
        Response response = target("profile/" + username + "/" +email).request().accept(MediaType.APPLICATION_JSON_TYPE).get();
        assertEquals("Getting subscriber failed", 200, response.getStatus());

        response = target("profile/" + username + "/" +email + "/suspend").request().accept(MediaType.APPLICATION_JSON_TYPE).get();
        assertEquals("Suspending subscriber failed", 200, response.getStatus());

        response = target("profile/" + username + "/" +email + "/resume").request().accept(MediaType.APPLICATION_JSON_TYPE).get();
        assertEquals("Resuming subscriber failed", 200, response.getStatus());
    }

    @Test(groups = {"Subscribers:CRUD"})
    public void crudSubscriberTest()
    {
        String new_email="test2@mail.com";
        String new_name="test_name";
        String new_rss="http://test.com/rss";
        SubscriberFactory factory=new SubscriberFactory();

        //create
        Form form_new = new Form();
        form_new.param("email", new_email);
        form_new.param("name", new_name);
        form_new.param("rss", new_rss);
        Response response = target("profile/" + username+ "/new").request().post(Entity.form(form_new), Response.class);
        assertEquals("Creating new subscriber failed", 200, response.getStatus());

        //read
        response = target("profile/"+username + "/" + new_email).request().accept(MediaType.APPLICATION_JSON_TYPE).get();
        assertEquals(200, response.getStatus());
        String entity=response.readEntity(String.class);
        Subscriber subscriber=factory.convertJson2Pojo(Subscriber.class,entity);
        assertEquals("Reading new subscriber failed", new_email, subscriber.getEmail());

        //update
        subscriber.setName(new_name);
        Form form_update = new Form();
        form_update.param("email", subscriber.getEmail());
        form_update.param("name", subscriber.getName());
        form_update.param("rss", new_rss);
        response = target("profile/" + username + "/update").request().post(Entity.form(form_update), Response.class);
        assertEquals(200, response.getStatus());

        //read
        response = target("profile/"+ username + "/" + new_email).request().accept(MediaType.APPLICATION_JSON_TYPE).get();
        assertEquals(200, response.getStatus());
        entity=response.readEntity(String.class);
        subscriber=factory.convertJson2Pojo(Subscriber.class, entity);
        assertEquals("Reading updated User failed", new_name, subscriber.getName());

        //delete
        response = target("profile/" + username + "/" + new_email + "/remove").request().accept(MediaType.APPLICATION_JSON_TYPE).get();
        assertEquals(200, response.getStatus());

    }

    @Test(groups = {"Subscriptions:GET"})
    public void getAllSubscriptionsTest()
    {
        final Response response = target("profile/" + username + "/" + email + "/subscriptions").request().accept(MediaType.APPLICATION_JSON_TYPE).get();

        assertEquals("Getting all subscriptions failed", 200, response.getStatus());
    }

    @Test(groups = {"Subscriptions:POST"})
    public void subscriptionOperationsTest()
    {
        String subscription="http://new_test.com/rss";
        //subscribe
        Form form = new Form();
        form.param("rss", subscription);
        Response response = target("profile/" + username + "/"+ email + "/subscribe").request().post(Entity.form(form), Response.class);
        assertEquals("Adding new subscription failed", 200, response.getStatus());

        //unsubscribe
        Form form_update = new Form();
        form_update.param("rss", subscription);
        response = target("profile/" + username + "/" + email + "/unsubscribe").request().post(Entity.form(form_update), Response.class);
        assertEquals("Deleting subscription failed", 200, response.getStatus());
    }

}
