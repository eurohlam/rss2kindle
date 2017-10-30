package org.roag.security;

import org.roag.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Created by eurohlam on 26/10/2017.
 */
//@Service
public class SecUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(SecUserDetailsService.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        try {
            logger.error("TRY TO FIND USER {}", s);
            User user=userRepository.getUser(s);
            logger.error("FOUND USER {}", user.getUsername());
            SecUserDetails ud=new SecUserDetails(user);
            return ud;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new UsernameNotFoundException(e.getMessage());
        }
    }
}
