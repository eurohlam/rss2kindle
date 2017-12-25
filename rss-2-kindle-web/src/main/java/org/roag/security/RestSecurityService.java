package org.roag.security;

import org.roag.model.User;
import org.roag.service.SubscriberFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;

/**
 * Created by eurohlam on 25/12/17.
 */
@Service
public class RestSecurityService implements SecurityService
{

//    @Autowired
//    private AuthenticationManager authenticationManager;

//    @Autowired
//    private UserDetailsService userDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(SpringUserDetailsServiceImpl.class);


    private String restHost;

    private String restPort;

    private String restPath;

    @Autowired
    public RestSecurityService(@Value("${rest.host}")String restHost,  @Value("${rest.port}")String restPort, @Value("${rest.path}")String restPath)
    {
        this.restPath = restPath;
        this.restPort = restPort;
        this.restHost = restHost;
    }

    @Override
    public UserDetails findUser(String username) throws UsernameNotFoundException
    {
        //TODO: cache for Rest Client
        String response=ClientBuilder.newClient().target(restHost + ":" + restPort + restPath).path("users/"+username).request().get(String.class);
        if (!response.contains("Not Found")) {
            SubscriberFactory factory = new SubscriberFactory();
            User user=factory.convertJson2Pojo(User.class, response);
            logger.debug("User {} exists with roles {}", user.getUsername(), user.getRoles());
            SpringUserDetailsImpl ud=new SpringUserDetailsImpl(user);
            return ud;
        }
        else
            throw new UsernameNotFoundException("User " + username + " has not been found");
    }

    @Override
    public UserDetails registerUser(String username, String email, String password) throws AuthenticationServiceException
    {
        try {
            logger.debug("Sign-up a new User {}:{} with email {}", username, password, email);
            if (username == null || username.length() == 0)
                throw new AuthenticationServiceException("User can't be created due to username is null or empty");

            Form form = new Form();
            form.param("username", username);
            form.param("password", password);
            //TODO: cache for Rest Client
            Response response= ClientBuilder.newClient().target(restHost + ":" + restPort + restPath).path("users/add").request().post(Entity.form(form), Response.class);
            if (response.getStatus() == 200) {

                return autologin(username, password);
            }
            else
                throw new UsernameNotFoundException("User " + username + " has not been found");

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new AuthenticationServiceException(e.getMessage());
        }
    }

    @Override
    public UserDetails autologin(String username, String password) throws UsernameNotFoundException{
/*
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());

        authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        if (usernamePasswordAuthenticationToken.isAuthenticated()) {
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            logger.debug(String.format("Auto login %s successfully!", username));
        }
*/
        return null;
    }
}
