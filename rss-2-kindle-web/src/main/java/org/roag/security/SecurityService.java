package org.roag.security;

import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Created by eurohlam on 25/12/17.
 */
public interface SecurityService {

    UserDetails findUser(String username) throws UsernameNotFoundException;

    boolean isUserExist(String username);

    UserDetails registerUser(String username, String email, String password) throws AuthenticationServiceException;

    UserDetails autologin(String username, String password) throws UsernameNotFoundException;
}
