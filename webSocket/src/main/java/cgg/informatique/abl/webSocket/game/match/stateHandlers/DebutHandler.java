package cgg.informatique.abl.webSocket.game.match.stateHandlers;

import cgg.informatique.abl.webSocket.game.match.Match;
import cgg.informatique.abl.webSocket.game.match.MatchState;

public class DebutHandler extends MatchStateHandler {
    public DebutHandler(Match context) {
        super(context);
    }

    @Override
    public void handleTimeout() {
        getContext().setEtat(MatchState.CHOIX);
    }
}
