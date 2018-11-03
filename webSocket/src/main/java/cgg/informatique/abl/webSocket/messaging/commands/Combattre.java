package cgg.informatique.abl.webSocket.messaging.commands;

import cgg.informatique.abl.webSocket.dto.lobby.Lobby;
import cgg.informatique.abl.webSocket.dto.lobby.LobbyRole;
import cgg.informatique.abl.webSocket.dto.lobby.LobbyUserData;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize
public class Combattre extends Commande{
    public Combattre() {}

    @Override
    public void execute(LobbyCommandContext context) {
        Lobby lobby = context.getLobby();
        try {
            LobbyUserData lud = lobby.getLobbyUserData(getDe());

            if (lud.getRoleCombat() != LobbyRole.ARBITRE) throw new IllegalArgumentException("Doit être l'arbitre");
            lobby.startMatch();
        } catch (IllegalArgumentException | IllegalStateException e) {
            send("Échec du combat. Raison: " + e.getMessage(), context);
        }
    }
}
