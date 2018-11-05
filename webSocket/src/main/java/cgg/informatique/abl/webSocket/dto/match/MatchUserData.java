package cgg.informatique.abl.webSocket.dto.match;

import cgg.informatique.abl.webSocket.dto.SanitizedCompte;
import cgg.informatique.abl.webSocket.dto.UserBase;
import cgg.informatique.abl.webSocket.dto.lobby.LobbyRole;
import cgg.informatique.abl.webSocket.dto.lobby.LobbyUserData;
import cgg.informatique.abl.webSocket.entites.Compte;
import cgg.informatique.abl.webSocket.messaging.DonneesReponseCommande;
import cgg.informatique.abl.webSocket.messaging.commands.TypeCommande;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Objects;

public class MatchUserData extends UserBase {
    private Match match;
    private LobbyUserData user;
    private PlayerState state = PlayerState.ESTRADE;
    private boolean saluting;
    private Attack attack = Attack.RIEN;

    public MatchUserData(LobbyUserData user, Match match) {
        this.user = user;
        this.match = match;
    }

    public LobbyRole getRoleCombat(){
        return user.getRoleCombat();
    }

    public void setState(PlayerState state) {
        this.state = state;
        match.sendData(null, new DonneesReponseCommande(TypeCommande.POSITION, this, state));
    }

    public PlayerState getState() {
        return state;
    }

    public boolean isReadyForFight() {
        return state == PlayerState.TATAMI && saluting;
    }

    public void setSaluting(boolean saluting) {
        this.saluting = saluting;
        match.sendData(null, new DonneesReponseCommande(TypeCommande.SALUER, this));
    }

    public boolean isSaluting() {
        return saluting;
    }

    public Attack getAttack() {
        return attack;
    }

    public void setAttack(Attack attack) {
        this.attack = attack;
        match.sendData(null, new DonneesReponseCommande(TypeCommande.ATTAQUER, this));
    }

    public String getNom() {
        return user.getUser().getAlias();
    }

    public String getCourriel() {
        return user.getUser().getCourriel();
    }

    public SanitizedCompte getUser() {
        return this.user;
    }

    @JsonIgnore
    public Compte getCompte() {
        return this.user.getUser();
    }
}
