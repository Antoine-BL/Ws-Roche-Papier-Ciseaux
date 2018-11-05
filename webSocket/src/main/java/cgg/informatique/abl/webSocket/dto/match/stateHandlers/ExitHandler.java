package cgg.informatique.abl.webSocket.dto.match.stateHandlers;

import cgg.informatique.abl.webSocket.dto.match.Match;
import cgg.informatique.abl.webSocket.dto.match.MatchState;

public class ExitHandler extends MatchStateHandler {
    public ExitHandler(Match context) {
        super(context);
    }

    @Override
    public void handleTimeout() {
        getContext().setMatchState(MatchState.OVER);
    }
}