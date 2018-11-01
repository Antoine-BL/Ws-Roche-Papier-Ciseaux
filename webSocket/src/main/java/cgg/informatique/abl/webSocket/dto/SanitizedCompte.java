package cgg.informatique.abl.webSocket.dto;

import cgg.informatique.abl.webSocket.entites.Compte;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(as= SanitizedCompte.class)
@JsonDeserialize(as=Compte.class)
public interface SanitizedCompte {
    String getCourriel();
    String getAlias();
    Long getAvatarId();
    void setAvatarId(Long id);
    String getRole();
    String getGroupe();
}
