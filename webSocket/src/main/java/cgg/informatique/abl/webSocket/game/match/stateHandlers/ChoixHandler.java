package cgg.informatique.abl.webSocket.game.match.stateHandlers;

import cgg.informatique.abl.webSocket.game.match.Match;
import cgg.informatique.abl.webSocket.game.match.MatchState;

public class ChoixHandler extends MatchStateHandler {
    public ChoixHandler(Match context) {
        super(context);
    }

    @Override
    public void handleTimeout() {
        getContext().effectuerMatch();
        getContext().setEtat(MatchState.GAGNANT);
    }
}
