package org.roag.rest;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTestNg;
import org.roag.model.User;
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
public class UserManagerTest extends JerseyTestNg.ContainerPerClassTest
{
    private final static String PATH = "users/";

    private String username = "test";

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
        Response response = target(PATH + username).request().accept(MediaType.APPLICATION_JSON_TYPE).get();
        assertEquals("Getting user failed", 200, response.getStatus());

        response = target(PATH + username +"/lock").request().accept(MediaType.APPLICATION_JSON_TYPE).get();
        assertEquals("Suspending user failed", 200, response.getStatus());

        response = target(PATH + username + "/unlock").request().accept(MediaType.APPLICATION_JSON_TYPE).get();
        assertEquals("Resuming user failed", 200, response.getStatus());
    }

    @Test(groups = {"UserManager:CRUD"})
    public void crudTestHtmlForm()
    {
        String new_user="formUser";
        String new_email = "html@mail.com";
        String new_password="htmlforever";
        SubscriberFactory factory=new SubscriberFactory();

        //create from html form
        Form form_new = new Form();
        form_new.param("username", new_user);
        form_new.param("email", new_email);
        form_new.param("password", "12345");
        Response response = target(PATH + "new").request().post(Entity.form(form_new), Response.class);
        assertEquals("Creating new User failed", 200, response.getStatus());

        //read
        response = target(PATH + new_user).request().accept(MediaType.APPLICATION_JSON_TYPE).get();
        assertEquals(200, response.getStatus());
        String entity=response.readEntity(String.class);
        User user=factory.convertJson2Pojo(User.class,entity);
        assertEquals("Reading new User failed", new_user, user.getUsername());

        //update
        user.setPassword(new_password);
        Form form_update = new Form();
        form_update.param("username", user.getUsername());
        form_update.param("password", user.getPassword());
        response = target(PATH + "update").request().post(Entity.form(form_update), Response.class);
        assertEquals(200, response.getStatus());

        //read
        response = target(PATH + new_user).request().accept(MediaType.APPLICATION_JSON_TYPE).get();
        assertEquals(200, response.getStatus());
        entity=response.readEntity(String.class);
        user=factory.convertJson2Pojo(User.class, entity);
        assertEquals("Reading updated User failed", new_password, user.getPassword());

        //delete
        response = target(PATH + new_user + "/remove").request().accept(MediaType.APPLICATION_JSON_TYPE).get();
        assertEquals(200, response.getStatus());

    }

    @Test(groups = {"UserManager:CRUD"})
    public void crudTestJson()
    {
        String new_user="jsonUser";
        String new_email = "json@mail.com";
        String new_password="jsonforever";
        SubscriberFactory factory=new SubscriberFactory();

        //create from json
        User u=factory.newUser(new_user, new_email, "12345");
        Response response = target(PATH + "new").request().post(Entity.json(factory.convertPojo2Json(u)), Response.class);
        assertEquals("Creating new User failed", 200, response.getStatus());

        //read
        response = target(PATH + new_user).request().accept(MediaType.APPLICATION_JSON_TYPE).get();
        assertEquals(200, response.getStatus());
        String entity=response.readEntity(String.class);
        User user=factory.convertJson2Pojo(User.class,entity);
        assertEquals("Reading new User failed", new_user, user.getUsername());

        //update
        user.setPassword(new_password);
        user.getSubscribers().add(factory.newSubscriber("json@json.org", "json", "http://json.org"));
        response = target(PATH + "update").request().post(Entity.json(factory.convertPojo2Json(user)), Response.class);
        assertEquals(200, response.getStatus());

        //read
        response = target(PATH + new_user).request().accept(MediaType.APPLICATION_JSON_TYPE).get();
        assertEquals(200, response.getStatus());
        entity=response.readEntity(String.class);
        user=factory.convertJson2Pojo(User.class, entity);
        assertEquals("Reading updated User failed", new_password, user.getPassword());

        //delete
        response = target(PATH + new_user + "/remove").request().accept(MediaType.APPLICATION_JSON_TYPE).get();
        assertEquals(200, response.getStatus());

    }
}
