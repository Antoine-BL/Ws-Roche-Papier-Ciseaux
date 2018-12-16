package cgg.informatique.abl.webSocket.entites;

import cgg.informatique.abl.webSocket.controleurs.rest.CompteController;
import cgg.informatique.abl.webSocket.dto.SanitizedCompte;
import cgg.informatique.abl.webSocket.dto.SanitizedUser;
import cgg.informatique.abl.webSocket.dto.UserBase;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

@Entity
@Table(name="COMPTES")
@JsonSerialize(as=SanitizedCompte.class)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "courriel")
public class Compte extends UserBase implements UserDetails, SanitizedCompte {
    @Id
    @Column(name="USERNAME")
    private String courriel;
    @Column(name="PASSWORD")
    private String motPasse;
    @Column(name="FULLNAME")
    private String alias;

    @ManyToOne(targetEntity = Avatar.class)
    @JoinColumn(name="AVATAR_ID")
    private Avatar avatar;

    @ManyToOne(targetEntity = Role.class)
    @JoinColumn(name="ROLE_ID")
    private Role role;

    @ManyToOne(targetEntity = Groupe.class)
    @JoinColumn(name="GROUPE_ID")
    private Groupe groupe;

    @Column(name="CHOUCHOU")
    private int chouchou;
    @Column(name="ENTRAINEMENT")
    private int entrainement;
    @Column(name="TALENT")
    private int talent;

    @OneToMany(mappedBy = "blanc",targetEntity = Combat.class)
    private List<Combat> combatsBlanc;

    @OneToMany(mappedBy = "rouge",targetEntity = Combat.class)
    private List<Combat> combatsRouge;

    @OneToMany(mappedBy = "arbitre",targetEntity = Combat.class)
    private List<Combat> combatsArbitre;

    @OneToMany(mappedBy = "professeur",targetEntity = Examen.class)
    private List<Examen> examensProf;

    @OneToMany(mappedBy = "eleve",targetEntity = Examen.class)
    private List<Examen> examensEleve;

    @Transient
    private Integer points = null;
    @Transient
    private Integer credits = null;

    protected Compte() { }

    Compte(
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
        return new CompteBuilder();
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

    public void setCombatsBlanc(List<Combat> combatsBlanc) {
        this.combatsBlanc = combatsBlanc;
    }

    public void setCombatsRouge(List<Combat> combatsRouge) {
        this.combatsRouge = combatsRouge;
    }

    public void setCombatsArbitre(List<Combat> combatsArbitre) {
        this.combatsArbitre = combatsArbitre;
    }

    public void setExamensProf(List<Examen> examensProf) {
        this.examensProf = examensProf;
    }

    public void setExamensEleve(List<Examen> examensEleve) {
        this.examensEleve = examensEleve;
    }

    @Override
    @JsonIgnore
    public boolean isDeshonore() {
        if (this.examensEleve == null) return false;
        return !this.examensEleve
                .stream()
                .max(Comparator.comparingLong(Examen::getTemps))
                .map(Examen::isReussi)
                .orElse(true);
    }

    @Override
    @JsonIgnore
    public int getPoints() {
        return CompteController.getPointsPour(this);
    }

    @JsonIgnore
    @Override
    public int getCredits() {
        return CompteController.getCreditsPour(this);
    }

    public List<Examen> getExamensProf() {
        return this.examensProf;
    }

    public List<Examen> getExamensEleve() {
        return this.examensEleve;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setGroupe(Groupe groupe) {
        this.groupe = groupe;
    }

    public void setMotPasse(String motPasse) {
        this.motPasse = motPasse;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public void setAvatar(Avatar avatar) {
        this.avatar = avatar;
    }

    public interface CourrielBuilder{
        MotPasseBuilder avecCourriel(String courriel);
    }

    public interface MotPasseBuilder {
        CompteBuilder avecMotDePasse(String motPasse);
    }
}


