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
        String new_user="test2";
        String new_password="new_password";
        SubscriberFactory factory=new SubscriberFactory();

        //create
        Form form_new = new Form();
        form_new.param("username", new_user);
        form_new.param("password", "12345");
        Response response = target("users/new").request().post(Entity.form(form_new), Response.class);
        assertEquals("Creating new User failed", 200, response.getStatus());

        //read
        response = target("users/"+new_user).request().accept(MediaType.APPLICATION_JSON_TYPE).get();
        assertEquals(200, response.getStatus());
        String entity=response.readEntity(String.class);
        User User=factory.convertJson2Pojo(User.class,entity);
        assertEquals("Reading new User failed", new_user, User.getUsername());

        //update
        User.setPassword(new_password);
        Form form_update = new Form();
        form_update.param("username", User.getUsername());
        form_update.param("password", User.getPassword());
        response = target("users/update").request().post(Entity.form(form_update), Response.class);
        assertEquals(200, response.getStatus());

        //read
        response = target("users/"+new_user).request().accept(MediaType.APPLICATION_JSON_TYPE).get();
        assertEquals(200, response.getStatus());
        entity=response.readEntity(String.class);
        User=factory.convertJson2Pojo(User.class, entity);
        assertEquals("Reading updated User failed", new_password, User.getPassword());

        //delete
        response = target("users/" + new_user + "/remove").request().accept(MediaType.APPLICATION_JSON_TYPE).get();
        assertEquals(200, response.getStatus());

    }

}
