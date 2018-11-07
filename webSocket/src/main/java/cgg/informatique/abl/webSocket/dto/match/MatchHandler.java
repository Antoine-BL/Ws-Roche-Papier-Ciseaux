package cgg.informatique.abl.webSocket.dto.match;

import cgg.informatique.abl.webSocket.dto.lobby.LobbyUserData;
import cgg.informatique.abl.webSocket.entites.Combat;
import cgg.informatique.abl.webSocket.messaging.DonneesReponseCommande;

public interface MatchHandler {
    void matchEnded(Combat combat);
    void sendData(String message, DonneesReponseCommande reponse);
    void send(String message);
    void quitter(LobbyUserData userData);
}
