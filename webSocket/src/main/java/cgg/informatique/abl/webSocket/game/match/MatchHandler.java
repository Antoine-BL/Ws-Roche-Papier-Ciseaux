package cgg.informatique.abl.webSocket.game.match;

import cgg.informatique.abl.webSocket.game.lobby.LobbyUserData;
import cgg.informatique.abl.webSocket.entites.Combat;
import cgg.informatique.abl.webSocket.messaging.DonneesReponseCommande;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public interface MatchHandler {
    void matchEnded();
    void saveMatch(Combat combat);
    void sendData(String message, DonneesReponseCommande reponse);
    void send(String message);
    void quitter(LobbyUserData userData);
}
