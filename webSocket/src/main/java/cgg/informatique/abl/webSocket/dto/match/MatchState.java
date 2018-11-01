package cgg.informatique.abl.webSocket.dto.match;

public enum MatchState {
    WAITING(15, "Que les combattants se présentent"),
    READY(15, "Rei!"),
    START(3, "Hajime!"),
    DECIDE(5, "L'arbitre doit décider d'un verdict"),
    EXIT(2, "Les combattants peuvent quitter"),
    OVER(0, "Fin du match. Les joueurs sont retournés à leurs positions");
    private static final long SECOND = 1000;
    private long duration;
    private String transitionMessage;

    MatchState(int nbSeconds, String transitionMessage) {
        duration = nbSeconds * SECOND;
        this.transitionMessage = transitionMessage;
    }

    public long getDuration() {
        return duration;
    }

    public String getTransitionMessage() {
        return transitionMessage;
    }
}
