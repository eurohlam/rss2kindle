package org.roag.security;

import org.roag.model.Roles;
import org.roag.model.User;
import org.roag.model.UserStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by eurohlam on 26/10/2017.
 */
public class SpringUserDetailsImpl implements UserDetails{

    private User user;

    public SpringUserDetailsImpl(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> set=new HashSet<GrantedAuthority>(1);
        user.getRoles().forEach(role -> set.add(new SimpleGrantedAuthority(role.toString())));
        return set;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserStatus.fromValue(user.getStatus())==UserStatus.ACTIVE;
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserStatus.fromValue(user.getStatus())==UserStatus.ACTIVE;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserStatus.fromValue(user.getStatus())==UserStatus.ACTIVE;
    }

    @Override
    public boolean isEnabled() {
        return UserStatus.fromValue(user.getStatus())==UserStatus.ACTIVE;
    }
}
