package org.roag.rest;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTestNg;
import org.roag.model.User;
import org.roag.service.SubscriberFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

/**
 * Created by eurohlam on 05.09.17.
 */
public class UserManagerTest extends JerseyTestNg.ContainerPerClassTest
{
    private String username="test";

    @Override
    protected Application configure()
    {
        return new ResourceConfig(UserManager.class);
    }

    @Test(groups = {"UserManager:GET"})
    public void getAllUsersTest()
    {
        final Response response = target("users").request().accept(MediaType.APPLICATION_JSON_TYPE).get();

        assertEquals(200, response.getStatus());
    }

    @Test(groups = {"UserManager:GET"})
    public void getUserOperationsTest()
    {
        Response response = target("users/"+username).request().accept(MediaType.APPLICATION_JSON_TYPE).get();
        assertEquals("Getting user failed", 200, response.getStatus());

        response = target("users/" + username +"/lock").request().accept(MediaType.APPLICATION_JSON_TYPE).get();
        assertEquals("Suspending user failed", 200, response.getStatus());

        response = target("users/" + username + "/unlock").request().accept(MediaType.APPLICATION_JSON_TYPE).get();
        assertEquals("Resuming user failed", 200, response.getStatus());
    }

    @Test(groups = {"UserManager:CRUD"})
    public void crudTest()
    {
/*
        String new_email="test2@mail.com";
        SubscriberFactory factory=new SubscriberFactory();

        //create
        Form form_new = new Form();
        form_new.param("email", new_email);
        form_new.param("name", "test2");
        form_new.param("rss", "http://test.com/rss");
        Response response = target("Users/new").request().post(Entity.form(form_new), Response.class);
        assertEquals("Creating new User failed", 200, response.getStatus());

        //read
        response = target("Users/"+new_email).request().accept(MediaType.APPLICATION_JSON_TYPE).get();
        assertEquals(200, response.getStatus());
        String entity=response.readEntity(String.class);
        System.out.println(entity);
        User User=factory.convertJson2Pojo(User.class,entity);
        assertEquals("Reading new User failed", new_email, User.getEmail());

        //update
        User.setName("test2_updated");
        Form form_update = new Form();
        form_update.param("email", User.getEmail());
        form_update.param("name", User.getName());
        form_update.param("rss", User.getRsslist().get(0).getRss()+"/updated");
        response = target("Users/update").request().post(Entity.form(form_update), Response.class);
        assertEquals(200, response.getStatus());

        //read
        response = target("Users/"+new_email).request().accept(MediaType.APPLICATION_JSON_TYPE).get();
        assertEquals(200, response.getStatus());
        entity=response.readEntity(String.class);
        System.out.println(entity);
        User=factory.convertJson2Pojo(User.class, entity);
        assertEquals("Reading updated User failed", "test2_updated", User.getName());

        //delete
        response = target("Users/" + new_email + "/remove").request().accept(MediaType.APPLICATION_JSON_TYPE).get();
        assertEquals(200, response.getStatus());

*/
    }

}
