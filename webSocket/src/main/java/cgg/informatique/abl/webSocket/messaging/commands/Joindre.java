package cgg.informatique.abl.webSocket.messaging.commands;

import cgg.informatique.abl.webSocket.game.lobby.Lobby;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize
public class Joindre extends Commande{
    public Joindre() {}

    @Override
    public void execute(LobbyCommandContext context) {
        try {
            Lobby lobby = LobbyCommandContext.getLobby();

            lobby.connect(this.getDe());

            lobby.getLobbyUserData(getDe()).sentCommand();
        } catch (Exception e) {
            e.printStackTrace();
            send("Échec de la connexion au lobby... Raison: " + e.getMessage(), context);
        }
    }
}
