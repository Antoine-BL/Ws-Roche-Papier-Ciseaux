package cgg.informatique.abl.webSocket.game.match;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(as = SerializableMatch.class)
public interface SerializableMatch {
    MatchUserData getArbitre();
    MatchUserData getBlanc();
    MatchUserData getRouge();
    MatchState getMatchState();
    Long getChrono();
}
