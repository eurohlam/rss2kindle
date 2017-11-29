package org.roag.security;

import org.roag.ds.UserRepository;
import org.roag.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Created by eurohlam on 26/10/2017.
 * Only for initial security test. Will be deleted
 */
//@Service
@Deprecated
public class SecUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(SecUserDetailsService.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        try {
            logger.debug("Trying to find a User {} in repository", s);
            User user=userRepository.getUser(s);
            logger.debug("User {} exists with roles {}", user.getUsername(), user.getRoles());
            SecUserDetails ud=new SecUserDetails(user);
            return ud;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new UsernameNotFoundException(e.getMessage());
        }
    }
}
