package cgg.informatique.abl.webSocket.messaging.commands;

import cgg.informatique.abl.webSocket.game.lobby.Lobby;
import cgg.informatique.abl.webSocket.game.lobby.LobbyUserData;
import cgg.informatique.abl.webSocket.game.match.Match;
import cgg.informatique.abl.webSocket.game.match.MatchUserData;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize()
public class Saluer  extends Commande{

    @Override
    public void execute(LobbyCommandContext context) {
        Lobby lobby = LobbyCommandContext.getLobby();
        Match match = lobby.getCurrentMatch();
        LobbyUserData lud = lobby.getLobbyUserData(getDe());

        MatchUserData mud = match.getParticipant(lud);

        mud.setSaluting(true);

        send(lud.getUser().getAlias() + " salue son adversaire", context);
    }
}
