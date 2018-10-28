package cgg.informatique.abl.webSocket.dto;

import cgg.informatique.abl.webSocket.entites.Compte;
import cgg.informatique.abl.webSocket.entites.Groupe;
import cgg.informatique.abl.webSocket.entites.Role;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;

public class SanitaryCompte {
    private Long id;
    private String courriel;
    private String alias;
    private String avatar;
    private Role role;
    private Groupe groupe;
    private int points;
    private int credits;

    public SanitaryCompte() {}

    public SanitaryCompte(Compte compte) {
        id = compte.getId();
        courriel = compte.getUsername();
        alias = compte.getAlias();
        avatar = compte.getAvatar();
        role = compte.getRole();
        groupe = compte.getGroupe();
        points = compte.getPoints();
        credits = compte.getCredits();
    }

    public Long getId() {
        return id;
    }

    public String getCourriel() {
        return courriel;
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

    public int getPoints() {
        return points;
    }

    public int getCredits() {
        return credits;
    }
}
