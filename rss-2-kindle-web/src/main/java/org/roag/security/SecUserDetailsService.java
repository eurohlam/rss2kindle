package org.roag.security;

import org.roag.ds.SubscriberRepository;
import org.roag.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 * Created by eurohlam on 26/10/2017.
 */
@Component
public class SecUserDetailsService implements UserDetailsService {

    @Autowired
    private SubscriberRepository subscriberRepository;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        try {
            User user=subscriberRepository.getUser(s);
            SecUserDetails ud=new SecUserDetails(user);
            return ud;
        } catch (Exception e) {
            throw new UsernameNotFoundException(e.getMessage());
        }
    }
}
