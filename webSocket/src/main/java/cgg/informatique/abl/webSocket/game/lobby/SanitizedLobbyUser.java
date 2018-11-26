package cgg.informatique.abl.webSocket.game.lobby;

import cgg.informatique.abl.webSocket.dto.SanitizedCompte;
import cgg.informatique.abl.webSocket.dto.SanitizedUser;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(as=SanitizedLobbyUser.class)
public interface SanitizedLobbyUser extends SanitizedUser {
    LobbyRole getRoleCombat();
}
