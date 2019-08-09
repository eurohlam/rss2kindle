package org.roag.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Created by eurohlam on 16/11/2017.
 */
public class SpringUserDetailsServiceImpl implements UserDetailsService {

    private final Logger logger = LoggerFactory.getLogger(SpringUserDetailsServiceImpl.class);

    @Autowired
    private SecurityService securityService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.debug("Trying to find User {} in repository", username);
        if (username == null || username.length() == 0) {
            throw new UsernameNotFoundException("User can't be found due to username is null or empty");
        }

        return securityService.findUser(username);
    }

}