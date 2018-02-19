package org.roag.web;

import org.roag.model.Roles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by eurohlam on 19/02/2018.
 */
@Controller
@RequestMapping("rest/profile")
//@Secured(Roles.ROLE_USER)
public class RestController
{
    @Autowired
    private RestClient client;

    @RequestMapping(value = "/service", method = RequestMethod.GET)
    public String servicePage(@PathVariable("viewId")ModelMap model)
    {
        return "service";
    }
}
