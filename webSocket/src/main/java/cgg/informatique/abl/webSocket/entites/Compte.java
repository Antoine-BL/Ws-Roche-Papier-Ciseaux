package cgg.informatique.abl.webSocket.entites;

import cgg.informatique.abl.webSocket.dto.CompteDto;
import cgg.informatique.abl.webSocket.dto.SanitizedCompte;
import cgg.informatique.abl.webSocket.dto.UserBase;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name="COMPTES")
public class Compte extends UserBase implements UserDetails, SanitizedCompte {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String courriel;
    private String motPasse;
    private String alias;
    @ManyToOne(targetEntity = Avatar.class)
    @JoinColumn(name="AVATAR")
    private Avatar avatar;
    @ManyToOne(targetEntity = Role.class)
    @JoinColumn(name="ROLE")
    private Role role;
    @ManyToOne(targetEntity = Groupe.class)
    @JoinColumn(name="GROUPE")
    private Groupe groupe;
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "blanc",targetEntity = Combat.class)
    @JsonManagedReference
    private List<Combat> combatsBlanc;
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "rouge",targetEntity = Combat.class)
    @JsonManagedReference
    private List<Combat> combatsRouge;
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "arbitre",targetEntity = Combat.class)
    @JsonManagedReference
    private List<Combat> combatsArbitre;

    protected Compte() { }

    private Compte(
            final @NotNull @NotEmpty String courriel,
            final @NotNull @NotEmpty String motPasse,
            final String alias,
            final Avatar avatar,
            final Role role,
            final Groupe groupe
    ) {
        this.courriel = courriel;
        this.motPasse = motPasse;
        this.alias = alias;
        this.avatar = avatar;
        if (role != null)
            this.role = role;
        if (groupe != null)
            this.groupe = groupe;
    }

    public SanitizedCompte sanitize() {
        return this;
    }

    public static CourrielBuilder Builder() {
        return new Builder();
    }

    public static Builder Builder(CompteDto compteDto) {
        return new Builder(compteDto);
    }

    public Long getId() {
        return id;
    }

    @Override
    public String getCourriel() {
        return courriel;
    }

    public String getAlias() {
        return alias;
    }

    @Override
    public Long getAvatarId() {
        return this.avatar.getId();
    }

    @Override
    public void setAvatarId(Long id) {
        if (this.avatar == null) avatar = new Avatar(id);
        else avatar.setId(id);
    }

    public Avatar getAvatar() {
        return avatar;
    }

    public String getRole() {
        return role.getAuthority();
    }

    public String getGroupe() {
        return groupe.getAuthority();
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

    public Groupe getGroupeObj() {
        return this.groupe;
    }

    @Override
    public List<Combat> getCombatsBlanc() {
        return combatsBlanc;
    }

    @Override
    public List<Combat> getCombatsRouge() {
        return combatsRouge;
    }

    @Override
    public List<Combat> getCombatsArbitre() {
        return combatsArbitre;
    }

    public static class Builder implements CourrielBuilder, MotPasseBuilder{
        private String courriel;
        private String motPasse;
        private String alias;
        private Avatar avatar;
        private Role role;
        private Groupe groupe;

        private Builder() { }

        private Builder(CompteDto compteDto) {
            this.courriel = compteDto.getCourriel();
            this.motPasse = compteDto.getPassword();
            this.alias = compteDto.getAlias();
            this.avatar = new Avatar(compteDto.getAvatar());
        }


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

        public Builder avecAvatar(Avatar avatar) {
            this.avatar = avatar;
            return this;
        }

        public Builder avecAvatar(String avatar) {
            this.avatar = new Avatar(avatar);
            return this;
        }

        public Compte build() {
            return new Compte(courriel, motPasse, alias, avatar, role, groupe);
        }
    }

    public interface CourrielBuilder{
        MotPasseBuilder avecCourriel(String courriel);
    }

    public interface MotPasseBuilder {
        Builder avecMotDePasse(String motPasse);
    }
}


