package cgg.informatique.abl.webSocket.messaging.commands;

import cgg.informatique.abl.webSocket.dto.Lobby;
import cgg.informatique.abl.webSocket.dto.LobbyUserData;
import cgg.informatique.abl.webSocket.dto.Match;
import cgg.informatique.abl.webSocket.dto.MatchUserData;
import cgg.informatique.abl.webSocket.messaging.Reponse;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize()
public class Saluer  extends Commande{

    @Override
    public Reponse execute(LobbyCommandContext context) {
        Lobby lobby = context.getLobby();
        Match match = lobby.getCurrentMatch();
        LobbyUserData lud = lobby.getLobbyUserData(getDe());

        MatchUserData mud = match.getParticipant(lud);

        mud.setSaluting(true);

        return new Reponse(1L, lud.getUser().getAlias() + " salue son adversaire");
    }
}
