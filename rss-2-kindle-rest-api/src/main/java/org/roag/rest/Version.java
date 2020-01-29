package org.roag.rest;

import org.springframework.stereotype.Service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Created by eurohlam on 29/01/20.
 */
@Service
@Path("")
public class Version {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getVersion() {
        return "{application: 'rss2kindle-api', version: 'v1'}";
    }
}
