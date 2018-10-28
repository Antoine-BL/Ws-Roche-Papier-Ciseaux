package cgg.informatique.abl.webSocket.messaging.commands;

import cgg.informatique.abl.webSocket.dto.Lobby;
import cgg.informatique.abl.webSocket.entites.Compte;
import cgg.informatique.abl.webSocket.messaging.Reponse;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

@JsonDeserialize
public class Joindre extends Commande{
    public Joindre() {}

    @Override
    public Reponse execute(LobbyCommandContext context) {
        try {
            Lobby lobby = context.getLobby();

            lobby.connect(this.getDe());

            lobby.getLobbyUserData(getDe()).sentCommand();

            return new Reponse(1L, "Vous avez rejoint le lobby!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Reponse(1L, "Échec de la connexion au lobby... Raison: " + e.getMessage());
        }
    }
}
