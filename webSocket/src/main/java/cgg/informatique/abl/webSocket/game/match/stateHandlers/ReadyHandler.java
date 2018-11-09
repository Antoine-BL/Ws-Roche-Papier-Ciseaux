package cgg.informatique.abl.webSocket.game.match.stateHandlers;

import cgg.informatique.abl.webSocket.game.lobby.LobbyRole;
import cgg.informatique.abl.webSocket.game.match.Match;
import cgg.informatique.abl.webSocket.game.match.MatchUserData;

public class ReadyHandler extends MatchStateHandler {
    public ReadyHandler(Match context) {
        super(context);
    }

    @Override
    public void handleTimeout() {
        getContext().determineFault();
    }

    @Override
    public void handleStateChanged() {
        Match match = getContext();
        MatchUserData rouge = match.getRouge();
        MatchUserData blanc = match.getBlanc();

        if (rouge.getRoleCombat() != LobbyRole.ROUGE || blanc.getRoleCombat()  != LobbyRole.BLANC){
            match.refAtFault();
        }
    }
}