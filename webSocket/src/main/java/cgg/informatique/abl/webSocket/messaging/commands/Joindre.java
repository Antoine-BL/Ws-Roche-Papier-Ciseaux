package cgg.informatique.abl.webSocket.messaging.commands;

import cgg.informatique.abl.webSocket.game.lobby.Lobby;
import cgg.informatique.abl.webSocket.game.lobby.LobbyPosition;
import cgg.informatique.abl.webSocket.game.lobby.LobbyRole;
import cgg.informatique.abl.webSocket.game.lobby.LobbyUserData;
import cgg.informatique.abl.webSocket.messaging.DonneesReponseCommande;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize
public class Joindre extends Commande{
    public Joindre() {}

    @Override
    public void execute(LobbyCommandContext context) {
        Lobby lobby = LobbyCommandContext.getLobby();

        try {
            LobbyUserData lud = lobby.getLobbyUserData(getDe());
            lud.sentCommand();
            lobby.sendData("nouveau spectateur", new DonneesReponseCommande(TypeCommande.JOINDRE, lobby.getLobbyUserData(getDe()), lobby.asSerializable()));
            lud.becomeRole(new LobbyPosition(LobbyRole.SPECTATEUR));
        } catch (Exception e) {
            e.printStackTrace();
            lobby.sendData("Échec de la connexion au lobby... Raison: " + e.getMessage(), new DonneesReponseCommande(TypeCommande.ERREUR));
        }
    }
}
