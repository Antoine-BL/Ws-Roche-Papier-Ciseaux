package cgg.informatique.abl.webSocket.messaging.commands;

import cgg.informatique.abl.webSocket.controleurs.webSocket.FightController;
import cgg.informatique.abl.webSocket.dto.Lobby;
import cgg.informatique.abl.webSocket.dto.LobbyRole;
import cgg.informatique.abl.webSocket.dto.LobbyUserData;
import cgg.informatique.abl.webSocket.entites.Compte;
import cgg.informatique.abl.webSocket.messaging.Reponse;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

@JsonDeserialize
public class Combattre extends Commande{
    public Combattre() {}

    @Override
    public Reponse execute(LobbyCommandContext context) {
        Lobby lobby = context.getLobby();
        try {
            LobbyUserData lud = lobby.getLobbyUserData(getDe());

            if (lud.getRole() != LobbyRole.ARBITRE) throw new IllegalArgumentException("Doit être l'arbitre");
            lobby.startMatch();
        } catch (IllegalArgumentException e) {
            return new Reponse(1L, "Échec du combat. Raison: " + e.getMessage());
        }

        return null;
    }
}
