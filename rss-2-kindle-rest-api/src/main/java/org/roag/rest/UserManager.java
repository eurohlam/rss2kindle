package org.roag.rest;

import org.roag.ds.OperationResult;
import org.roag.ds.UserRepository;
import org.roag.model.User;
import org.roag.service.ModelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by eurohlam on 09.11.16.
 */
@Service
@Path("users")
public class UserManager {

    private final Logger logger = LoggerFactory.getLogger(UserManager.class);

    @Autowired
    private UserRepository userRepository;

    private ModelFactory modelFactory;

    private List<String> validationErrors = Collections.EMPTY_LIST;

    public UserManager() {
        super();
        this.modelFactory = new ModelFactory();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUsers() {
        logger.debug("Fetch all users from repository");
        try {
            String users = modelFactory.pojo2Json(userRepository.findAll());
            return Response.ok(users, MediaType.APPLICATION_JSON_TYPE).build();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @GET
    @Path("/{username: [a-zA-Z_.0-9]*}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUser(@PathParam("username") String id) {
        logger.debug("Fetch user {} from repository", id);
        try {
            String user = modelFactory.pojo2Json(userRepository.getUser(id));
            return Response.ok(user, MediaType.APPLICATION_JSON_TYPE).build();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @GET
    @Path("/{username: [a-zA-Z_.0-9]*}/lock")
    @Produces(MediaType.APPLICATION_JSON)
    public Response lockUser(@PathParam("username") String id) {
        logger.warn("Lock user {}", id);
        try {
            OperationResult result = userRepository.lockUser(id);
            if (result == OperationResult.SUCCESS) {
                return Response.ok(result.toJson(), MediaType.APPLICATION_JSON_TYPE).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GET
    @Path("/{username: [a-zA-Z_.0-9]*}/unlock")
    @Produces(MediaType.APPLICATION_JSON)
    public Response unlockUser(@PathParam("username") String id) {
        logger.warn("Unlock user {}", id);
        try {
            OperationResult result = userRepository.unlockUser(id);
            if (result == OperationResult.SUCCESS) {
                return Response.ok(result.toJson(), MediaType.APPLICATION_JSON_TYPE).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @POST
    @Path("/new")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addUser(@FormParam("username") String username,
                            @FormParam("email") String email,
                            @FormParam("password") String password) {
        logger.info("Add new user {}", username);
        try {
            OperationResult result = userRepository.addUser(modelFactory.newUser(username, email, password));
            logger.info(result.toString());
            if (result == OperationResult.SUCCESS) {
                return Response.ok(result.toJson(), MediaType.APPLICATION_JSON_TYPE).build();
            } else {
                return Response.status(Response.Status.CONFLICT).build();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @POST
    @Path("/new")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addUser(String message) {
        logger.info("Requested to add new user from data {}", message);
        try {
            User user = modelFactory.json2Pojo(User.class, message);
            if (!isValid(user)) {
                return Response.status(Response.Status.BAD_REQUEST.getStatusCode(), validationErrors.toString()).build();
            }
            OperationResult result = userRepository.addUser(user);
            logger.info(result.toString());
            if (result == OperationResult.SUCCESS) {
                return Response.ok(result.toJson(), MediaType.APPLICATION_JSON_TYPE).build();
            } else {
                return Response.status(Response.Status.CONFLICT).build();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @POST
    @Path("/update")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUser(@FormParam("username") String username,
                               @FormParam("password") String password) {
        logger.warn("Update existing user {}", username);
        try {
            User user = userRepository.getUser(username);
            user.setPassword(password);
            OperationResult result = userRepository.updateUser(user);
            if (result == OperationResult.SUCCESS) {
                return Response.ok(result.toJson(), MediaType.APPLICATION_JSON_TYPE).build();
            } else {
                return Response.status(Response.Status.CONFLICT).build();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PUT
    @Path("/update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUser(String message) {
        logger.warn("Requested to update existing user with data {}", message);
        try {
            User user = modelFactory.json2Pojo(User.class, message);
            OperationResult result = userRepository.updateUser(user);
            if (result == OperationResult.SUCCESS) {
                return Response.ok(result.toJson(), MediaType.APPLICATION_JSON_TYPE).build();
            } else {
                return Response.status(Response.Status.CONFLICT).build();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DELETE
    @Path("/{username: [a-zA-Z_.0-9]*}/remove")
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeUser(@PathParam("username") String id) {
        logger.warn("Remove user {}", id);
        try {
            OperationResult result = userRepository.removeUser(id);
            if (result == OperationResult.SUCCESS) {
                return Response.ok(result.toJson(), MediaType.APPLICATION_JSON_TYPE).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    private boolean isValid(User user) {
        List<String> errors = new ArrayList<>();
        boolean isValid = true;
        if (user.getUsername().isEmpty()) {
            errors.add("username must not be empty");
            isValid = false;
        }
        if (user.getPassword().isEmpty()) {
            errors.add("password must not be empty");
            isValid = false;
        }
        if (user.getEmail().isEmpty()) {
            errors.add("email must not be empty");
            isValid = false;
        }
        if (user.getPassword().length() < 6) {
            errors.add("password must not be less then 6 symbols");
            isValid = false;
        }
        if (!user.getUsername().matches("[a-zA-Z_.0-9]*")) {
            errors.add("username contains unexpected symbols. It must satisfy the mask [a-zA-Z_.0-9]*");
            isValid = false;
        }
        if (!user.getEmail().matches("[\\w.]+@[\\w.]+")) {
            errors.add("email contains unexpected symbols. It must satisfy the mask [\\w.]+@[\\w.]+");
            isValid = false;
        }
        if (isValid) {
            validationErrors = Collections.EMPTY_LIST;
        } else {
            validationErrors = errors;
        }
        return isValid;
    }

}
