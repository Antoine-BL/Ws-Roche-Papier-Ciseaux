package cgg.informatique.abl.webSocket.messaging.commands;

import cgg.informatique.abl.webSocket.dto.lobby.Lobby;
import cgg.informatique.abl.webSocket.dto.lobby.LobbyPosition;
import cgg.informatique.abl.webSocket.dto.lobby.LobbyRole;
import cgg.informatique.abl.webSocket.dto.lobby.LobbyUserData;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize()
public class ChangerRole extends Commande{
    private static final int ROLE_INDEX = 0;
    private static final int POSITION = 1;

    public ChangerRole() {}
    public ChangerRole(String role) { }

    @Override
    public void execute(LobbyCommandContext context) {
        try {
            if (parametres.size() >= 1) {
                LobbyRole role = LobbyRole.valueOf(parametres.get(ROLE_INDEX).toUpperCase());
                Lobby lobby = context.getLobby();
                LobbyUserData user = lobby.getLobbyUserData(getDe());

                LobbyPosition position = new LobbyPosition(role);
                if (parametres.size() >= 2 && parametres.get(POSITION) != null) {
                    int index = Integer.parseInt(parametres.get(POSITION));
                    position.setPosition(index);
                }

                user.checkCanGoTo(position);
                user.becomeRole(position);

                lobby.getLobbyUserData(getDe()).sentCommand();
            } else {
                Lobby lobby = context.getLobby();
                LobbyUserData lud = lobby.getLobbyUserData(getDe());
                LobbyRole role = lud.getRoleCombat();

                send("Role est: " + role, context);
            }
        } catch (NumberFormatException e) {
            send("Echec du changement de rôle. Raison: La position donnée doit être un eniter valide", context);
        } catch (Exception e){
            send("Echec du changement de rôle. Raison: " + e.getMessage(), context);
        }
    }
}
