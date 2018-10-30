package cgg.informatique.abl.webSocket.dto;

import cgg.informatique.abl.webSocket.entites.Compte;
import cgg.informatique.abl.webSocket.entites.Groupe;
import cgg.informatique.abl.webSocket.entites.Role;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;

@JsonDeserialize(as=SanitaryCompte.class)
public interface SanitaryCompte {
    public String getUsername();
    public String getAlias();
    public Long getAvatarId();
    public Role getRole();
    public Groupe getGroupe();
    public int getPoints();
    public int getCredits();
}
