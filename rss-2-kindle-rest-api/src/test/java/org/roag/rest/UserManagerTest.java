package org.roag.rest;

import com.github.javafaker.Faker;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTestNg;
import org.roag.model.User;
import org.roag.service.ModelFactory;
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
public class UserManagerTest extends JerseyTestNg.ContainerPerClassTest {

    private static final String PATH = "users/";

    private String username = "test";

    @Override
    protected Application configure() {
        return new ResourceConfig(UserManager.class);
    }

    @Test(groups = {"UserManager:GET"})
    public void getAllUsersTest() {
        final Response response = target("users").request().accept(MediaType.APPLICATION_JSON_TYPE).get();

        assertEquals(200, response.getStatus());
    }

    @Test(groups = {"UserManager:GET"})
    public void getUserOperationsTest() {
        Response response = target(PATH + username).request().accept(MediaType.APPLICATION_JSON_TYPE).get();
        assertEquals("Getting user failed", 200, response.getStatus());

        response = target(PATH + username + "/lock").request().accept(MediaType.APPLICATION_JSON_TYPE).get();
        assertEquals("Suspending user failed", 200, response.getStatus());

        response = target(PATH + username + "/unlock").request().accept(MediaType.APPLICATION_JSON_TYPE).get();
        assertEquals("Resuming user failed", 200, response.getStatus());
    }

    @Test(groups = {"UserManager:CRUD"})
    public void crudTestHtmlForm() {
        final Faker faker = new Faker();
        final String newEmail = faker.internet().emailAddress();
        final String newUser = faker.name().username();
        final String newPassword = faker.internet().password(6,8);
        final ModelFactory factory = new ModelFactory();

        //create from html form
        Form formNew = new Form();
        formNew.param("username", newUser);
        formNew.param("email", newEmail);
        formNew.param("password", faker.internet().password(6,8));
        Response response = target(PATH + "new").request().post(Entity.form(formNew), Response.class);
        assertEquals("Creating new User failed", 200, response.getStatus());

        //read
        response = target(PATH + newUser).request().accept(MediaType.APPLICATION_JSON_TYPE).get();
        assertEquals(200, response.getStatus());
        String entity = response.readEntity(String.class);
        User user = factory.json2Pojo(User.class, entity);
        assertEquals("Reading new User failed", newUser, user.getUsername());

        //update
        user.setPassword(newPassword);
        Form formUpdate = new Form();
        formUpdate.param("username", user.getUsername());
        formUpdate.param("password", user.getPassword());
        response = target(PATH + "update").request().post(Entity.form(formUpdate), Response.class);
        assertEquals(200, response.getStatus());

        //read
        response = target(PATH + newUser).request().accept(MediaType.APPLICATION_JSON_TYPE).get();
        assertEquals(200, response.getStatus());
        entity = response.readEntity(String.class);
        user = factory.json2Pojo(User.class, entity);
        assertEquals("Reading updated User failed", newPassword, user.getPassword());

        //delete
        response = target(PATH + newUser + "/remove").request().delete();
        assertEquals(200, response.getStatus());

    }

    @Test(groups = {"UserManager:CRUD"})
    public void crudTestJson() {
        final Faker faker = new Faker();
        final String newEmail = faker.internet().emailAddress();
        final String newUser = faker.name().username();
        final String newPassword = faker.internet().password(6,8);
        final ModelFactory factory = new ModelFactory();

        //create from json
        User u = factory.newUser(newUser, newEmail, faker.internet().password(6,8));
        Response response = target(PATH + "new").request().post(Entity.json(factory.pojo2Json(u)), Response.class);
        assertEquals("Creating new User failed: " + response.getStatusInfo().getReasonPhrase(), 200, response.getStatus());

        //read
        response = target(PATH + newUser).request().accept(MediaType.APPLICATION_JSON_TYPE).get();
        assertEquals(200, response.getStatus());
        String entity = response.readEntity(String.class);
        User user = factory.json2Pojo(User.class, entity);
        assertEquals("Reading new User failed", newUser, user.getUsername());

        //update
        user.setPassword(newPassword);
        user.getSubscribers().add(factory.newSubscriber("json@json.org", "json", "http://json.org"));
        response = target(PATH + "update").request().put(Entity.json(factory.pojo2Json(user)), Response.class);
        assertEquals(200, response.getStatus());

        //read
        response = target(PATH + newUser).request().accept(MediaType.APPLICATION_JSON_TYPE).get();
        assertEquals(200, response.getStatus());
        entity = response.readEntity(String.class);
        user = factory.json2Pojo(User.class, entity);
        assertEquals("Reading updated User failed", newPassword, user.getPassword());

        //delete
        response = target(PATH + newUser + "/remove").request().delete();
        assertEquals(200, response.getStatus());

    }
}
