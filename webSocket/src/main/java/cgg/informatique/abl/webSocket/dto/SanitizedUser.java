package cgg.informatique.abl.webSocket.dto;

import cgg.informatique.abl.webSocket.entites.Combat;
import cgg.informatique.abl.webSocket.entites.Compte;
import cgg.informatique.abl.webSocket.entites.Groupe;
import cgg.informatique.abl.webSocket.entites.Role;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;

@JsonSerialize(as= SanitizedUser.class)
@JsonDeserialize(as=Compte.class)
public interface SanitizedUser {
    Long getId();
    String getCourriel();
    String getAlias();
    Long getAvatarId();
    void setAvatarId(Long id);
    Role getRole();
    Groupe getGroupe();
}
