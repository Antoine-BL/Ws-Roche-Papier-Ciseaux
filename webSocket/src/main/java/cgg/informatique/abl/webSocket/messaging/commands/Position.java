package cgg.informatique.abl.webSocket.messaging.commands;

import cgg.informatique.abl.webSocket.dto.*;
import cgg.informatique.abl.webSocket.messaging.Reponse;

public class Position extends Commande{
    private static int POSITION = 0;

    @Override
    public Reponse execute(LobbyCommandContext context) {
        Lobby lobby = context.getLobby();
        Match match = lobby.getCurrentMatch();
        LobbyUserData lud = lobby.getLobbyUserData(getDe());

        MatchUserData mud = match.getParticipant(lud);

        if (parametres.size() == 1) {
            PlayerState state = PlayerState.valueOf(parametres.get(POSITION));

            mud.setState(state);

            return new Reponse(1L, lud.getUser().getAlias() + " s'est déplacé vers: " + state);
        }

        return new Reponse(1L, lud.getUser().getAlias() + " est dans la position: " + mud.getState());
    }
}
