package org.roag.rest;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTestNg;
import org.roag.model.Rss;
import org.roag.model.Subscriber;
import org.roag.service.SubscriberFactory;
import org.testng.annotations.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Form;
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

    @Test(groups = {"SubscriberManager:GET"})
    public void getAllSubscribersTest()
    {
        final Response response = target("subscribers").request().accept(MediaType.APPLICATION_JSON_TYPE).get();

        assertEquals(200, response.getStatus());
    }

    @Test(groups = {"SubscriberManager:GET"})
    public void getSubscriberOperationsTest()
    {
        Response response = target("subscribers/test@mail.com").request().accept(MediaType.APPLICATION_JSON_TYPE).get();
        assertEquals("Getting subscriber failed", 200, response.getStatus());

        response = target("subscribers/test@mail.com/suspend").request().accept(MediaType.APPLICATION_JSON_TYPE).get();
        assertEquals("Suspending subscriber failed", 200, response.getStatus());

        response = target("subscribers/test@mail.com/resume").request().accept(MediaType.APPLICATION_JSON_TYPE).get();
        assertEquals("Resuming subscriber failed", 200, response.getStatus());
    }

    @Test(groups = {"SubscriberManager:CRUD"})
    public void crudTest()
    {
        String new_email="test2@mail.com";
        SubscriberFactory factory=new SubscriberFactory();

        //create
        Form form_new = new Form();
        form_new.param("email", new_email);
        form_new.param("name", "test2");
        form_new.param("rss", "http://test.com/rss");
        Response response = target("subscribers/new").request().post(Entity.form(form_new), Response.class);
        assertEquals("Creating new subscriber failed", 200, response.getStatus());

        //read
        response = target("subscribers/"+new_email).request().accept(MediaType.APPLICATION_JSON_TYPE).get();
        assertEquals(200, response.getStatus());
        String entity=response.readEntity(String.class);
        System.out.println(entity);
        Subscriber subscriber=factory.convertJson2Pojo(Subscriber.class,entity);
        assertEquals("Reading new subscriber failed", new_email, subscriber.getEmail());

        //update
        subscriber.setName("test2_updated");
        Form form_update = new Form();
        form_update.param("email", subscriber.getEmail());
        form_update.param("name", subscriber.getName());
        form_update.param("rss", subscriber.getRsslist().get(0).getRss()+"/updated");
        response = target("subscribers/update").request().post(Entity.form(form_update), Response.class);
        assertEquals(200, response.getStatus());

        //read
        response = target("subscribers/"+new_email).request().accept(MediaType.APPLICATION_JSON_TYPE).get();
        assertEquals(200, response.getStatus());
        entity=response.readEntity(String.class);
        System.out.println(entity);
        subscriber=factory.convertJson2Pojo(Subscriber.class, entity);
        assertEquals("Reading updated subscriber failed", "test2_updated", subscriber.getName());

        //delete
        response = target("subscribers/" + new_email + "/remove").request().accept(MediaType.APPLICATION_JSON_TYPE).get();
        assertEquals(200, response.getStatus());

    }

}
