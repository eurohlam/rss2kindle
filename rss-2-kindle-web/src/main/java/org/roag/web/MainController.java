package org.roag.web;

import org.roag.security.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Controller
public class MainController {

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    @Autowired
    private SecurityService securityService;

    @RequestMapping(value = "/subscribers", method = RequestMethod.GET)
    public String subscribersPage(ModelMap model) {
        model.addAttribute("username", getPrincipal());
        return "subscribers";
    }

    @RequestMapping(value = "/service", method = RequestMethod.GET)
    public String servicePage(ModelMap model) {
        model.addAttribute("username", getPrincipal());
        return "service";
    }

    @RequestMapping(value = "/admin", method = RequestMethod.GET)
    public String adminPage(ModelMap model) {
        model.addAttribute("username", getPrincipal());
        return "admin/adminPage";
    }


    @RequestMapping(value ="/profile", method = RequestMethod.GET)
    public String profilePage(ModelMap model)
    {
        model.addAttribute("username", getPrincipal());
        return "profile";
    }


    @RequestMapping(value ="/login", method = RequestMethod.GET)
    public String login(@RequestParam(value = "error", required = false) String error)
    {
        if (SecurityContextHolder.getContext().getAuthentication().isAuthenticated())
            return "profile";
        else
            return "login";
    }

    @RequestMapping(value="/logout", method = RequestMethod.POST)
    public String logout(HttpServletRequest request, HttpServletResponse response)
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null){
            logger.info("Logout user {}", auth.getPrincipal());
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:login?logout";
    }

    private String getPrincipal(){
        String userName = null;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            userName = ((UserDetails)principal).getUsername();
        } else {
            userName = principal.toString();
        }
        return userName;
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String registerUser(@ModelAttribute("newUserForm") NewUserForm user, BindingResult result, ModelMap model)
    {
        if (result.hasErrors()) {
            for (ObjectError er:result.getAllErrors())
                logger.error(er.toString());
            return "error";
        }

        logger.info("Trying to register a new user {} with email {}", user.getUsername(), user.getEmail());
        if (!user.getPassword().equals(user.getConfirmPassword()))
        {
            logger.error("Password {} not verified by {}", user.getPassword(), user.getConfirmPassword());
            //TODO: verification error
        }

        securityService.registerUser(user.getUsername(), user.getEmail(), user.getPassword());

        return "registerResult";
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String showRegisterForm(Model model)
    {
        model.addAttribute("newUserForm", new NewUserForm());
        return "/register";
    }
}