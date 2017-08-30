package org.roag.rest;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTestNg;
import org.testng.annotations.Test;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import static org.junit.Assert.*;

/**
 * Created by eurohlam on 30.08.17.
 */
public class ProfileManagerTest extends JerseyTestNg.ContainerPerClassTest
{
    @Override
    protected Application configure()
    {
        return new ResourceConfig(ProfileManager.class);
    }

    @Test(groups = {"profile"})
    public void getAllSubscriptionTest()
    {
        final Response response = target().request().get();

        assertEquals(response.getStatus(), 200);
    }
}
