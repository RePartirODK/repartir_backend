package com.example.repartir_backend.entities;

import com.example.repartir_backend.enumerations.Role;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;


public class UserDetailsImpl implements UserDetails {
    @Getter
    private final int id;
    private final String email;
    private final String motDePasse;
    private final Role role;

    public UserDetailsImpl(Utilisateur utilisateur) {
        this.id = utilisateur.getId();
        this.email = utilisateur.getEmail();
        this.motDePasse = utilisateur.getMotDePasse();
        this.role = utilisateur.getRole();
    }

    public UserDetailsImpl(Admin admin) {
        this.id = admin.getId();
        this.email = admin.getEmail();
        this.motDePasse = admin.getMotDePasse();
        this.role = admin.getRole();
    }



    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }
    @Override
    public String getPassword() {
        return motDePasse;
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
