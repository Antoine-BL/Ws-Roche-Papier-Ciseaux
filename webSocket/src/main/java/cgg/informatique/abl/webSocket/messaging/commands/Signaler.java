package cgg.informatique.abl.webSocket.messaging.commands;

import cgg.informatique.abl.webSocket.game.lobby.Lobby;
import cgg.informatique.abl.webSocket.game.lobby.LobbyRole;
import cgg.informatique.abl.webSocket.game.match.Match;
import cgg.informatique.abl.webSocket.game.match.MatchState;
import cgg.informatique.abl.webSocket.game.match.Signal;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize()
public class Signaler extends Commande{
    private static int SIGNAL = 0;
    private static int CIBLE = 1;

    @Override
    public void execute(LobbyCommandContext context) {

    }
}
