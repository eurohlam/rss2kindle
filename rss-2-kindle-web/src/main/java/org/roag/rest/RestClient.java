package org.roag.rest;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by eurohlam on 22/03/2019.
 */
public interface RestClient {

    default Response sendRequest(WebTarget target, String path, RequestMethod method, String json) {
        Response response;
        if (method == RequestMethod.GET) {
            response = target.path(path).request().get(Response.class);
        } else if (method == RequestMethod.PUT) {
            response = target.path(path).request().put(Entity.json(json), Response.class);
        } else if (method == RequestMethod.DELETE) {
            response = target.path(path).request().delete(Response.class);
        } else if (method == RequestMethod.POST) {
            response = target.path(path).request().post(Entity.json(json), Response.class);
        } else {
            throw new IllegalArgumentException("Unsupported request method: " + method);
        }

        return response;
    }
}
