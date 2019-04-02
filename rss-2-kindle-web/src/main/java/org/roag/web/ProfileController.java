package org.roag.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by eurohlam on 19/02/2018.
 */
@Controller
public class ProfileController extends AbstractController {

    private final Logger logger = LoggerFactory.getLogger(ProfileController.class);

    @RequestMapping(value = "/subscribers", method = RequestMethod.GET)
    public String subscribersPage(ModelMap model) {
        return "subscribers";
    }

    @RequestMapping(value = "/subscriberDetails", method = RequestMethod.GET)
    public String subscriberDetailsPage(@RequestParam(name="subscriber") String subscriberId, ModelMap model) {
        model.addAttribute("subscriber", subscriberId);
        return "subscriberDetails";
    }

    @RequestMapping(value = "/service", method = RequestMethod.GET)
    public String servicePage(ModelMap model) {
        return "service";
    }

    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    public String profilePage(ModelMap model) {
        return "profile";
    }


}