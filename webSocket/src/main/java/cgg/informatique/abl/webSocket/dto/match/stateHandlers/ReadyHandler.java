package cgg.informatique.abl.webSocket.dto.match.stateHandlers;

import cgg.informatique.abl.webSocket.dto.lobby.LobbyRole;
import cgg.informatique.abl.webSocket.dto.lobby.LobbyUserData;
import cgg.informatique.abl.webSocket.dto.match.Match;
import cgg.informatique.abl.webSocket.dto.match.MatchUserData;

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

    }
}