package cgg.informatique.abl.webSocket.dto;

import cgg.informatique.abl.webSocket.entites.Combat;
import cgg.informatique.abl.webSocket.entites.Compte;
import cgg.informatique.abl.webSocket.entites.Groupe;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;

@JsonSerialize(as= SanitizedCompte.class)
@JsonDeserialize(as=Compte.class)
public interface SanitizedCompte {
    Long getId();
    String getCourriel();
    String getAlias();
    Long getAvatarId();
    void setAvatarId(Long id);
    String getRole();
    String getGroupe();
    Groupe getGroupeObj();
    List<Combat> getCombatsRouge();
    List<Combat> getCombatsBlanc();
    List<Combat> getCombatsArbitre();
}
