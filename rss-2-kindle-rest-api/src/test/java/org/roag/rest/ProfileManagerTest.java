package org.roag.rest;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTestNg;
import org.roag.model.Rss;
import org.roag.model.RssStatus;
import org.roag.model.Subscriber;
import org.roag.service.ModelFactory;
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
public class ProfileManagerTest extends JerseyTestNg.ContainerPerClassTest {

    private static final String PATH = "profile/";

    private String username = "test";
    private String email = "test@mail.com";

    @Override
    protected Application configure() {
        return new ResourceConfig(ProfileManager.class);
    }

    @Test(groups = {"Subscribers:GET"})
    public void getAllSubscribersTest() {
        final Response response = target(PATH + username).request().accept(MediaType.APPLICATION_JSON_TYPE).get();
        assertEquals("Getting all subscribers failed", 200, response.getStatus());
    }

    @Test(groups = {"Subscribers:GET"})
    public void getSubscriberOperationsTest() {
        Response response = target(PATH + username + "/" + email).request().accept(MediaType.APPLICATION_JSON_TYPE).get();
        assertEquals("Getting subscriber failed", 200, response.getStatus());

        response = target(PATH + username + "/" + email + "/suspend").request().accept(MediaType.APPLICATION_JSON_TYPE).get();
        assertEquals("Suspending subscriber failed", 200, response.getStatus());

        response = target(PATH + username + "/" + email + "/resume").request().accept(MediaType.APPLICATION_JSON_TYPE).get();
        assertEquals("Resuming subscriber failed", 200, response.getStatus());
    }

    @Test(groups = {"Subscribers:CRUD"})
    public void crudSubscriberTestHtmlForm() {
        final String newEmail = "test2@mail.com";
        final String newName = "test_name";
        final String newRss = "http://test.com/rss";
        final ModelFactory factory = new ModelFactory();

        //create
        Form formNew = new Form();
        formNew.param("email", newEmail);
        formNew.param("name", newName);
        formNew.param("rss", newRss);
        Response response = target(PATH + username + "/new").request().post(Entity.form(formNew), Response.class);
        assertEquals("Creating new subscriber failed", 200, response.getStatus());

        //read
        response = target(PATH + username + "/" + newEmail).request().accept(MediaType.APPLICATION_JSON_TYPE).get();
        assertEquals(200, response.getStatus());
        String entity = response.readEntity(String.class);
        Subscriber subscriber = factory.json2Pojo(Subscriber.class, entity);
        assertEquals("Reading new subscriber failed", newEmail, subscriber.getEmail());

        //update
        subscriber.setName(newName);
        Form formUpdate = new Form();
        formUpdate.param("email", subscriber.getEmail());
        formUpdate.param("name", subscriber.getName());
        formUpdate.param("rss", newRss);
        response = target(PATH + username + "/update").request().post(Entity.form(formUpdate), Response.class);
        assertEquals(200, response.getStatus());

        //read
        response = target(PATH + username + "/" + newEmail).request().accept(MediaType.APPLICATION_JSON_TYPE).get();
        assertEquals(200, response.getStatus());
        entity = response.readEntity(String.class);
        subscriber = factory.json2Pojo(Subscriber.class, entity);
        assertEquals("Reading updated User failed", newName, subscriber.getName());

        //delete
        response = target(PATH + username + "/" + newEmail + "/remove").request().delete();
        assertEquals(200, response.getStatus());

    }

    @Test(groups = {"Subscribers:CRUD"})
    public void crudSubscriberTestJson() {
        String newEmail = "test2@mail.com";
        String newName = "test_name";
        String newRss = "http://test.com/rss";
        ModelFactory factory = new ModelFactory();

        //create
        Response response = target(PATH + username + "/new")
                .request()
                .put(Entity.json(factory.pojo2Json(factory.newSubscriber(newEmail, newName, newRss))), Response.class);
        assertEquals("Creating new subscriber failed", 200, response.getStatus());

        //read
        response = target(PATH + username + "/" + newEmail).request().accept(MediaType.APPLICATION_JSON_TYPE).get();
        assertEquals(200, response.getStatus());
        String entity = response.readEntity(String.class);
        Subscriber subscriber = factory.json2Pojo(Subscriber.class, entity);
        assertEquals("Reading new subscriber failed", newEmail, subscriber.getEmail());

        //update
        Rss rss = new Rss();
        rss.setRss("http://newrss.com");
        rss.setStatus(RssStatus.ACTIVE.toString());
        subscriber.getRsslist().add(rss);
        subscriber.setName("Kindle");
        response = target(PATH + username + "/update").request().put(Entity.json(factory.pojo2Json(subscriber)), Response.class);
        assertEquals(200, response.getStatus());

        //read
        response = target(PATH + username + "/" + newEmail).request().accept(MediaType.APPLICATION_JSON_TYPE).get();
        assertEquals(200, response.getStatus());
        entity = response.readEntity(String.class);
        subscriber = factory.json2Pojo(Subscriber.class, entity);
        assertEquals("Reading updated User failed", 2, subscriber.getRsslist().size());
        assertEquals("Reading updated User failed", "Kindle", subscriber.getName());

        //delete
        response = target(PATH + username + "/" + newEmail + "/remove").request().delete();
        assertEquals(200, response.getStatus());

    }

    @Test(groups = {"Subscriptions:GET"})
    public void getAllSubscriptionsTest() {
        final Response response = target(PATH + username + "/" + email + "/subscriptions")
                .request()
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .get();

        assertEquals("Getting all subscriptions failed", 200, response.getStatus());
    }

    @Test(groups = {"Subscriptions:POST"})
    public void subscriptionOperationsTest() {
        String subscription = "http://new_test.com/rss";
        //subscribe
        Form form = new Form();
        form.param("rss", subscription);
        Response response = target(PATH + username + "/" + email + "/subscribe").request().post(Entity.form(form), Response.class);
        assertEquals("Adding new subscription failed", 200, response.getStatus());

        //unsubscribe
        Form formUpdate = new Form();
        formUpdate.param("rss", subscription);
        response = target(PATH + username + "/" + email + "/unsubscribe").request().post(Entity.form(formUpdate), Response.class);
        assertEquals("Deleting subscription failed", 200, response.getStatus());
    }

}
