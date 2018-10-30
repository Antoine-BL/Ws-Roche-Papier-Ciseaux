package cgg.informatique.abl.webSocket.configurations.http;

import cgg.informatique.abl.webSocket.entites.Avatar;
import cgg.informatique.abl.webSocket.entites.Compte;
import cgg.informatique.abl.webSocket.entites.Groupe;
import cgg.informatique.abl.webSocket.entites.Role;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.constraints.NotNull;
import java.util.Collection;

public class UserDetailsImpl implements UserDetails {
    private Compte compte;

    UserDetailsImpl(@NotNull Compte compte) {
        if (compte == null) throw new IllegalArgumentException();

        this.compte = compte;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return compte.getAuthorities();
    }

    public Compte getCompte() {
        return compte;
    }

    @Override
    public String getPassword() { return compte.getPassword();}
    @Override
    public String getUsername() { return compte.getUsername();}
    @Override
    public boolean isAccountNonExpired() { return compte.isAccountNonExpired();}
    @Override
    public boolean isAccountNonLocked() { return compte.isAccountNonLocked();}
    @Override
    public boolean isCredentialsNonExpired() { return
            compte.isCredentialsNonExpired();
    }
    @Override
    public boolean isEnabled() { return compte.isEnabled(); }
}