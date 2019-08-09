package org.roag.security;

import org.roag.model.User;
import org.roag.rest.EmailRestClient;
import org.roag.service.ModelFactory;
import org.roag.rest.AdminRestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.Response;
import java.time.LocalDateTime;

/**
 * Created by eurohlam on 25/12/17.
 */
@Service
public class RestSecurityService implements SecurityService {

    private final Logger logger = LoggerFactory.getLogger(RestSecurityService.class);

    private ModelFactory modelFactory = new ModelFactory();

    @Autowired
    private AdminRestClient adminClient;

    @Autowired
    private EmailRestClient emailClient;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public RestSecurityService() {

    }

    @Override
    public UserDetails findUser(String username) throws UsernameNotFoundException {
        Response response = adminClient.getUser(username);
        if (response.getStatus() == 200) {
            User user = modelFactory.json2Pojo(User.class, response.readEntity(String.class));
            logger.debug("User {} exists with roles {}", user.getUsername(), user.getRoles());
            user.setLastLogin(LocalDateTime.now().toString());
            adminClient.updateUser(modelFactory.pojo2Json(user));
            UserDetails ud = new SpringUserDetailsImpl(user);
            return ud;
        } else {
            throw new UsernameNotFoundException("User " + username + " has not been found");
        }
    }

    @Override
    public UserDetails registerUser(String username, String email, String password) throws AuthenticationServiceException {
        logger.debug("Sign-up a new User {}:{} with email {}", username, password, email);
        if (username == null || username.length() == 0) {
            throw new AuthenticationServiceException("User can't be created due to username is null or empty");
        }

        User user = modelFactory.newUser(username, email, passwordEncoder != null ? passwordEncoder.encode(password) : password);
        logger.info("Sending request to create a new user {} with email {} to REST service", username, email);
        Response response = adminClient.addUser(modelFactory.pojo2Json(user));
        logger.info("Response from REST service {} ", response);
        if (response.getStatus() == 200) {
            String registrationSubject = "Confirmation of registration in service RSS-2-KINDLE ";
            String registrationConfirmation = "Dear " + username + ",\n" +
                    "\nWe are happy to confirm your registration in service RSS-2-KINDLE.\n" +
                    "\nUsername=" + username + "\nPassword=" + password + "\n\nRoundkick Studio";
            response = emailClient.sendEmailToUser(username, registrationSubject, registrationConfirmation);
            logger.debug("Response from REST email service {} ", response);
            return autologin(username, password);
        } else {
            throw new UsernameNotFoundException("User " + username + " can't be created due to error "
                    + response.readEntity(String.class));
        }
    }

    @Override
    public UserDetails autologin(String username, String password) throws UsernameNotFoundException {
        /*
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
            new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());

        authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        if (usernamePasswordAuthenticationToken.isAuthenticated()) {
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            logger.debug(String.format("Auto login %s successfully!", username));
        }
        */
        return null;
    }

    @Override
    public boolean isUserExist(String username) {
        Response response = adminClient.getUser(username);
        return (response.getStatus() == 200);
    }

}
