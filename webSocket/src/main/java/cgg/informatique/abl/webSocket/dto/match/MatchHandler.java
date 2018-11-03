package cgg.informatique.abl.webSocket.dto.match;

import cgg.informatique.abl.webSocket.messaging.DonneesReponseCommande;

public interface MatchHandler {
    void matchEnded();
    void sendData(String message, DonneesReponseCommande reponse);
    void send(String message);
}
