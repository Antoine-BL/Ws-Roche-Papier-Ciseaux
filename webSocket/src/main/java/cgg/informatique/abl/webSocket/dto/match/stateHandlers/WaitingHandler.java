package cgg.informatique.abl.webSocket.dto.match.stateHandlers;

import cgg.informatique.abl.webSocket.dto.lobby.LobbyRole;
import cgg.informatique.abl.webSocket.dto.match.Match;
import cgg.informatique.abl.webSocket.dto.match.MatchUserData;

public class WaitingHandler extends MatchStateHandler {
    public WaitingHandler(Match context) {
        super(context);
    }

    @Override
    public void handleTimeout() {
        getContext().disqualify();
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
