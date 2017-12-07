package org.roag.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;


@Controller
public class MainController {

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);


    @RequestMapping(value = "/subscribers")
    public String subscribersPage() {
        return "subscribers";
    }

    @RequestMapping(value = "/service")
    public String servicePage() {
        return "service";
    }

    @RequestMapping(value = "/admin")
    public String adminPage() {
        return "admin/adminPage";
    }

    @RequestMapping(value ="/profile", method = RequestMethod.GET)
    public ModelAndView login(@RequestParam(value = "error", required = false) String error) {
        ModelAndView model = new ModelAndView();
        if (error != null) {
            model.addObject("error", "Invalid username or password!");
            return model; //TODO: unclear code
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
//         auth.getPrincipal().toString()
//            auth.getCredentials()
//         auth.getDetails()
            if (auth.getPrincipal() instanceof UserDetails) {
                UserDetails ud = (UserDetails)auth.getPrincipal();
                logger.info("User {}:{} just logged in", ud.getUsername(), ud.getPassword());
                model.addObject("username", ud.getUsername());
                model.setViewName("profile");
                return model;
            }
        }
        model.addObject("error", "Authentication error");
        return model;
    }

}