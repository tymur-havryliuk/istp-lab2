package com.istp.api.security;

import com.istp.api.common.UserRole;
import com.istp.api.dao.persistence.entity.UserEntity;
import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@RequiredArgsConstructor
public class AppUserPrincipal implements UserDetails, AuthenticatedUser {
    private final Long id;
    private final String email;
    private final String passwordHash;
    private final UserRole role;
    private final boolean active;

    public static AppUserPrincipal from(UserEntity user) {
        return new AppUserPrincipal(
                user.getId(),
                user.getEmail(),
                user.getPasswordHash(),
                user.getRole(),
                user.isActive()
        );
    }

    public static AppUserPrincipal testTokenUser(Long id, String email, UserRole role) {
        return new AppUserPrincipal(id, email, "", role, true);
    }

    @Override
    public Long id() {
        return id;
    }

    @Override
    public String email() {
        return email;
    }

    @Override
    public UserRole role() {
        return role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }
}
