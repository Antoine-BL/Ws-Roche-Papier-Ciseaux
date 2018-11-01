package cgg.informatique.abl.webSocket.messaging.commands;

import cgg.informatique.abl.webSocket.dto.lobby.Lobby;
import cgg.informatique.abl.webSocket.dto.lobby.LobbyUserData;
import cgg.informatique.abl.webSocket.dto.match.Match;
import cgg.informatique.abl.webSocket.dto.match.MatchUserData;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize()
public class Saluer  extends Commande{

    @Override
    public void execute(LobbyCommandContext context) {
        Lobby lobby = context.getLobby();
        Match match = lobby.getCurrentMatch();
        LobbyUserData lud = lobby.getLobbyUserData(getDe());

        MatchUserData mud = match.getParticipant(lud);

        mud.setSaluting(true);

        send(lud.getUser().getAlias() + " salue son adversaire", context);
    }
}
