package org.roag.security;

import org.roag.ds.UserRepository;
import org.roag.model.User;
import org.roag.service.SubscriberFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.ws.rs.client.ClientBuilder;

/**
 * Created by eurohlam on 16/11/2017.
 */
public class RestUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(RestUserDetailsService.class);

    private String restHost;
    private String restPort;
    private String restPath;

    public RestUserDetailsService(String restHost, String restPort, String restPath) {
        this.restPath = restPath;
        this.restPort = restPort;
        this.restHost = restHost;
    }

    //    @Autowired
//    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            logger.debug("Trying to find a User {} in repository", username);
            String response=ClientBuilder.newClient().target(restHost + ":" + restPort + restPath).path("users/"+username).request().get(String.class);
            if (!response.contains("Not Found")) {
                SubscriberFactory factory = new SubscriberFactory();
                User user=factory.convertJson2Pojo(User.class, response);
                logger.debug("User {} exists with roles {}", user.getUsername(), user.getRoles());
                SecUserDetails ud=new SecUserDetails(user);
                return ud;
            }
            else
                throw new UsernameNotFoundException("User " + username + " has not been found");

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new UsernameNotFoundException(e.getMessage());
        }
    }
}