package cgg.informatique.abl.webSocket.messaging.commands;

import cgg.informatique.abl.webSocket.controleurs.webSocket.FightController;
import cgg.informatique.abl.webSocket.dto.Lobby;
import cgg.informatique.abl.webSocket.dto.LobbyRole;
import cgg.informatique.abl.webSocket.dto.LobbyUserData;
import cgg.informatique.abl.webSocket.entites.Compte;
import cgg.informatique.abl.webSocket.messaging.Reponse;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

@JsonDeserialize()
public class Role extends Commande{
    private static final int ROLE_INDEX = 0;

    public Role() {

    }

    public Role(Compte de, List<String> params) {
        super(de, params);
        typeCommande = TypeCommande.LOBBYMESSAGE;
    }

    @Override
    public Reponse execute(FightController context) {
        if (parametres.size() == 1) {
            try {
                LobbyRole role = LobbyRole.valueOf(parametres.get(ROLE_INDEX).toUpperCase());

                role.changeRole(context.getLobby(), getDe());

                return new Reponse(1L, "Role changé à: " + role);
            } catch (Exception e){
                return new Reponse(1L, "Echec du changement de rôle. Raison: " + e.getMessage());
            }
        } else {
            Lobby lobby = context.getLobby();
            LobbyUserData lub = lobby.getLobbyUserData(getDe());
            LobbyRole role = lub.getRole();

            return new Reponse(1L, "Role est: " + role);
        }
    }
}
