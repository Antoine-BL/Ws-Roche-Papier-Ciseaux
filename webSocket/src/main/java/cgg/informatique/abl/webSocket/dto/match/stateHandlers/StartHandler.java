package cgg.informatique.abl.webSocket.dto.match.stateHandlers;

import cgg.informatique.abl.webSocket.dto.match.Match;

public class StartHandler extends MatchStateHandler {
    public StartHandler(Match context) {
        super(context);
    }

    @Override
    public void handleTimeout() {
        getContext().handleRound();
    }
}
