package cgg.informatique.abl.webSocket.dto;

import cgg.informatique.abl.webSocket.entites.Combat;
import cgg.informatique.abl.webSocket.entites.Compte;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;

@JsonSerialize(as= SanitizedCompte.class)
@JsonDeserialize(as= Compte.class)
public interface SanitizedCompte extends SanitizedUser {
    List<Combat> getCombatsRouge();
    List<Combat> getCombatsBlanc();
    List<Combat> getCombatsArbitre();
    boolean isDeshonore();
    int getPoints();
    int getCredits();
}
