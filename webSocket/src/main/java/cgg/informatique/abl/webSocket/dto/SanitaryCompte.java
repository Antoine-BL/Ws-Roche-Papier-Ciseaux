package cgg.informatique.abl.webSocket.dto;

import cgg.informatique.abl.webSocket.entites.Compte;
import cgg.informatique.abl.webSocket.entites.Groupe;
import cgg.informatique.abl.webSocket.entites.Role;
import cgg.informatique.abl.webSocket.messaging.commands.CommandDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;

@JsonSerialize(as=SanitaryCompte.class)
@JsonDeserialize(as=Compte.class)
public interface SanitaryCompte {
    String getCourriel();
    String getAlias();
    Long getAvatarId();
    void setAvatarId(Long id);
    Role getRole();
    Groupe getGroupe();
    int getPoints();
    int getCredits();
}
