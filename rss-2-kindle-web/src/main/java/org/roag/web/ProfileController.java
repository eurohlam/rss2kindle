package org.roag.web;

import org.roag.security.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by eurohlam on 19/02/2018.
 */
@Controller
public class ProfileController extends AbstractController {

    private final Logger logger = LoggerFactory.getLogger(ProfileController.class);

    @Autowired
    private SecurityService securityService;

    @RequestMapping(value = "/subscribers", method = RequestMethod.GET)
    public String subscribersPage(ModelMap model) {
        return "subscribers";
    }

    @RequestMapping(value = "/service", method = RequestMethod.GET)
    public String servicePage(ModelMap model) {
        return "service";
    }

    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    public String profilePage(ModelMap model) {
        return "profile";
    }


    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(@RequestParam(value = "error", required = false) String error) {
        if (SecurityContextHolder.getContext().getAuthentication().isAuthenticated())
            return "profile";
        else
            return "login";
    }

    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            logger.info("Logout user {}", auth.getPrincipal());
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:login?logout";
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String registerUser(@ModelAttribute("newUserForm") NewUserForm user, BindingResult result, ModelMap model) {
        NewUserFormValidator validator = new NewUserFormValidator(securityService);
        validator.validate(user, result);

        if (result.hasErrors()) {
            logger.error("Registration of a new user {} with email {} failed due to validation errors:", user.getUsername(), user.getEmail());
            result.getAllErrors().forEach(error -> logger.error(error.toString()));
            return "register";
        }

        securityService.registerUser(user.getUsername(), user.getEmail(), user.getPassword());
        model.addAttribute("username", user.getUsername());
        model.addAttribute("email", user.getEmail());
        return "registrationResult";
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String showRegisterForm(Model model) {
        model.addAttribute("newUserForm", new NewUserForm());
        return "/register";
    }
}