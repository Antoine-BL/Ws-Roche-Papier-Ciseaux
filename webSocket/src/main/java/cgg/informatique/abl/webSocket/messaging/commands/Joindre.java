package cgg.informatique.abl.webSocket.messaging.commands;

import cgg.informatique.abl.webSocket.game.lobby.Lobby;
import cgg.informatique.abl.webSocket.game.lobby.LobbyPosition;
import cgg.informatique.abl.webSocket.game.lobby.LobbyRole;
import cgg.informatique.abl.webSocket.game.lobby.LobbyUserData;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize
public class Joindre extends Commande{
    public Joindre() {}

    @Override
    public void execute(LobbyCommandContext context) {
        try {
            Lobby lobby = LobbyCommandContext.getLobby();

            LobbyUserData lud = lobby.getLobbyUserData(getDe());
            lud.sentCommand();
            lud.becomeRole(new LobbyPosition(LobbyRole.SPECTATEUR));
        } catch (Exception e) {
            e.printStackTrace();
            send("Échec de la connexion au lobby... Raison: " + e.getMessage(), context);
        }
    }
}
