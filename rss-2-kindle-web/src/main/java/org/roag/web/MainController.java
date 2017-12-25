package org.roag.web;

import org.roag.model.User;
import org.roag.security.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;


@Controller
public class MainController {

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    @Autowired
    private SecurityService securityService;

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
            model.addObject("error", error);
            return model; //TODO: unclear code
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
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

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String registerUser(@ModelAttribute("newUserForm")NewUserForm user, BindingResult result, ModelMap model)
    {
        if (result.hasErrors()) {
            return "error";
        }

        logger.info("Trying to register a new user {}:{} with email {}", user.getUsername(), user.getPassword(), user.getEmail());
        if (!user.getPassword().equals(user.getConfirmPassword()))
        {
            logger.error("Password {} not verified by {}", user.getPassword(), user.getConfirmPassword());
            //TODO: verification error
        }

        securityService.registerUser(user.getUsername(), user.getEmail(), user.getPassword());

        return "registerResult";
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String showForm(Model model)
    {
        model.addAttribute("newUserForm", new NewUserForm());
        return "/register";
    }
}