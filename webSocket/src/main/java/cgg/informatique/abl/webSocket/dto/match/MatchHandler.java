package cgg.informatique.abl.webSocket.dto.match;

public interface MatchHandler {
    void matchEnded();
    void sendMessage(String transitionMessage);
    void sendRound(Attack blancAttack, Attack rougeAttack);
}
