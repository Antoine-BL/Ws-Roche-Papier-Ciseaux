package cgg.informatique.abl.webSocket.game.match.stateHandlers;

import cgg.informatique.abl.webSocket.game.match.Match;
import cgg.informatique.abl.webSocket.game.match.MatchUserData;

public class StartHandler extends MatchStateHandler {
    public StartHandler(Match context) {
        super(context);
    }

    @Override
    public void handleTimeout() {
        getContext().handleRound();
    }

    @Override
    public void handleStateChanged() {
        Match match = getContext();
        MatchUserData rouge = match.getRouge();
        MatchUserData blanc = match.getBlanc();

        if (!rouge.isReadyForFight() || !blanc.isReadyForFight()){
            match.refAtFault();
        }
    }
}
