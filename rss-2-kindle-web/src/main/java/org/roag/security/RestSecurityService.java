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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    private static final Logger logger = LoggerFactory.getLogger(RestSecurityService.class);

    private static final ThreadLocal<SimpleDateFormat> dateFormat = new ThreadLocal<SimpleDateFormat>(){
        @Override
        protected SimpleDateFormat initialValue()
        {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };

    private SubscriberFactory subscriberFactory = new SubscriberFactory();

    private String restHost;

    private String restPort;

    private String restPath;

    @Autowired
    private PasswordEncoder passwordEncoder;

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
        Client restClient = ClientBuilder.newClient();
        Response response = restClient.target(restHost + ":" + restPort + restPath).path("users/"+username).request().get();
        if (response.getStatus() == 200) {
            User user=subscriberFactory.convertJson2Pojo(User.class, response.readEntity(String.class));
            logger.debug("User {} exists with roles {}", user.getUsername(), user.getRoles());
//            user.setLastLogin(dateFormat.get().format(new Date()));
//            response= restClient.target(restHost + ":" + restPort + restPath).path("users/update").request().post(Entity.json(subscriberFactory.convertPojo2Json(user)), Response.class);
            UserDetails ud=new SpringUserDetailsImpl(user);
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

            User user = subscriberFactory.newUser(username, email, passwordEncoder!=null?passwordEncoder.encode(password):password);
            //TODO: cache for Rest Client
            logger.info("Sending request to create a new user {} with email {} to REST service {}:{}{}", username, email, restHost, restPort, restPath);
            Response response= ClientBuilder.newClient().target(restHost + ":" + restPort + restPath).path("users/new").request().post(Entity.json(subscriberFactory.convertPojo2Json(user)), Response.class);
            logger.info("Response from REST service {} ", response.toString());
            if (response.getStatus() == 200) {

                return autologin(username, password);
            }
            else
                throw new UsernameNotFoundException("User " + username + " can't be created due to error " + response.readEntity(String.class));

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
