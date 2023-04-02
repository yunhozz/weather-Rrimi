package com.authservice.common.security.session;

import com.authservice.common.enums.Provider;
import com.authservice.common.enums.Role;
import com.authservice.domain.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Getter
public class UserPrincipal implements UserDetails {

    private final String userId; // random UUID
    private final String email;
    private final String name;
    private final Set<Role> roles;
    private final Provider provider;

    public UserPrincipal(User user) {
        this.userId = user.getUserId();
        this.email = user.getEmail();
        this.name = user.getName();
        this.roles = user.getRoles();
        this.provider = user.getProvider();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return new HashSet<>() {{
            for (Role role : roles) {
                SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(role.getAuth());
                add(simpleGrantedAuthority);
            }
        }};
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}