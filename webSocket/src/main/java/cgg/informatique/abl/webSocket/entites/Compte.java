package cgg.informatique.abl.webSocket.entites;

import org.hibernate.annotations.ColumnDefault;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@Table(name="COMPTES")
public class Compte implements UserDetails {
    @Id
    @GeneratedValue
    private Long id;
    private String courriel;
    private String motPasse;
    private String alias;
    private String avatar;
    @Enumerated
    private Role role = Role.NOUVEAU;
    @Enumerated
    private Groupe groupe = Groupe.BLANC;

    protected Compte() { }

    private Compte(
            final Long id,
            final String courriel,
            final String motPasse,
            final String alias,
            final String avatar,
            final Role role,
            final Groupe groupe
    ) {
        ArrayList<String> errors = new ArrayList<>();

        this.id = id;
        this.courriel = courriel;
        this.motPasse = motPasse;
        this.alias = alias;
        this.avatar = avatar;
        if (role != null)
            this.role = role;
        if (groupe != null)
            this.groupe = groupe;

        Validate();
    }

    private void Validate() {
        if (courriel == null) {
            throw new IllegalArgumentException("Courriel ne peut pas être null");
        }

        if (motPasse == null) {
            throw new IllegalArgumentException("Mot de passe ne peut pas être null");
        }
    }

    public static CourrielBuilder Builder() {
        return new Builder();
    }

    public static CourrielBuilder Builder(Long id) {
        return new Builder(id);
    }

    public Long getId() {
        return id;
    }

    public String getCourriel() {
        return courriel;
    }

    public String getMotPasse() {
        return motPasse;
    }

    public String getAlias() {
        return alias;
    }

    public String getAvatar() {
        return avatar;
    }

    public Role getRole() {
        return role;
    }

    public Groupe getGroupe() {
        return groupe;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        ArrayList<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(role);
        authorities.add(groupe);

        return authorities;
    }

    @Override
    public String getPassword() {
        return motPasse;
    }

    @Override
    public String getUsername() {
        return courriel;
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

    public static class Builder implements CourrielBuilder, MotPasseBuilder{
        private Long id;
        private String courriel;
        private String motPasse;
        private String alias;
        private String avatar;
        private Role role;
        private Groupe groupe;

        private Builder() { }

        private Builder(Long id) { this.id = id; }

        public MotPasseBuilder avecCourriel(@NotNull String courriel) {
            this.courriel = courriel;
            return this;
        }

        public Builder avecMotDePasse(@NotNull String motPasse) {
            this.motPasse = motPasse;
            return this;
        }

        public Builder avecAlias(String alias) {
            this.alias = alias;
            return this;
        }

        public Builder avecRole(Role role) {
            this.role = role;
            return this;
        }

        public Builder avecGroupe(Groupe groupe) {
            this.groupe = groupe;
            return this;
        }

        public Builder avecAvatar(String avatar) {
            this.avatar = avatar;
            return this;
        }

        public Compte build() {
            return new Compte(id, courriel, motPasse, alias, avatar, role, groupe);
        }
    }

    public interface CourrielBuilder{
        MotPasseBuilder avecCourriel(String courriel);
    }

    public interface MotPasseBuilder {
        Builder avecMotDePasse(String motPasse);
    }
}


