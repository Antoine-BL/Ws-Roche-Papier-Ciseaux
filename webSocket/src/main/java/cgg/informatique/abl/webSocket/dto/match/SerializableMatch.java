package cgg.informatique.abl.webSocket.dto.match;

import cgg.informatique.abl.webSocket.dto.lobby.SerializableLobby;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(as = SerializableMatch.class)
public interface SerializableMatch {
    MatchUserData getArbitre();
    MatchUserData getBlanc();
    MatchUserData getRouge();
    MatchState getMatchState();
    Long getChrono();
}
