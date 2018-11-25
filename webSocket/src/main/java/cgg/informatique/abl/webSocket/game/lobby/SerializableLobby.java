package cgg.informatique.abl.webSocket.game.lobby;

import cgg.informatique.abl.webSocket.game.match.SerializableMatch;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(as = SerializableLobby.class)
public interface SerializableLobby {
    LobbyUserData[] getAilleurs();
    LobbyUserData[] getSpectateurs();
    LobbyUserData[] getCombattants();
    LobbyUserData getBlanc();
    LobbyUserData getRouge();
    LobbyUserData getArbitre();
    SerializableMatch getMatch();
    SerializableLobby asSerializable();
}
