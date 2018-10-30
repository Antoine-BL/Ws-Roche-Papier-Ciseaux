package cgg.informatique.abl.webSocket.messaging.commands;

import cgg.informatique.abl.webSocket.controleurs.webSocket.FightController;
import cgg.informatique.abl.webSocket.dto.Lobby;
import cgg.informatique.abl.webSocket.dto.LobbyUserData;
import cgg.informatique.abl.webSocket.entites.Compte;
import cgg.informatique.abl.webSocket.messaging.Reponse;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

@JsonDeserialize
public class Quitter extends Commande{
    public Quitter() { }

    @Override
    public Reponse execute(LobbyCommandContext context) {
        try {
            Lobby lobby = context.getLobby();

            LobbyUserData lub = lobby.getLobbyUserData(getDe());
            lobby.quitter(lub);

            return new Reponse(1L, "Lobby quitté avec succès");
        } catch (Exception e) {
            return new Reponse(1L, "Erreur en quittant le lobby");
        }
    }
}