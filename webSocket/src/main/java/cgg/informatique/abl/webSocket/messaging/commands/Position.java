package cgg.informatique.abl.webSocket.messaging.commands;

import cgg.informatique.abl.webSocket.game.lobby.Lobby;
import cgg.informatique.abl.webSocket.game.lobby.LobbyUserData;
import cgg.informatique.abl.webSocket.game.match.Match;
import cgg.informatique.abl.webSocket.game.match.MatchUserData;
import cgg.informatique.abl.webSocket.game.match.PlayerState;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize
public class Position extends Commande{
    private static int POSITION = 0;

    @Override
    public void execute(LobbyCommandContext context) {
        Lobby lobby = context.getLobby();
        Match match = lobby.getCurrentMatch();
        LobbyUserData lud = lobby.getLobbyUserData(getDe());

        MatchUserData mud = match.getParticipant(lud);

        if (parametres.size() == 1) {
            try {
                PlayerState state = PlayerState.valueOf(parametres.get(POSITION).toUpperCase());

                mud.setState(state);

                send(lud.getUser().getAlias() + " s'est déplacé vers: " + state, context);
            } catch (Exception e) {
                e.printStackTrace();
                send(e.getMessage(), context);
            }
        }

        send(lud.getUser().getAlias() + " est dans la position: " + mud.getState(), context);
    }
}
