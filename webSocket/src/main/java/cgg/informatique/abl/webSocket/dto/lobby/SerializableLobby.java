package cgg.informatique.abl.webSocket.dto.lobby;

import cgg.informatique.abl.webSocket.dto.SanitizedCompte;
import cgg.informatique.abl.webSocket.dto.match.SerializableMatch;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(as = SerializableLobby.class)
public interface SerializableLobby {
    LobbyUserData[] getSpectateurs();
    LobbyUserData[] getCombattants();
    LobbyUserData getBlanc();
    LobbyUserData getRouge();
    LobbyUserData getArbitre();
    SerializableMatch getMatch();
}
