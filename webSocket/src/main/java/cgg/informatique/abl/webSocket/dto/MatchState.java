package cgg.informatique.abl.webSocket.dto;

public enum MatchState {
    WAITING(15, "Que les combattants se présentent"),
    READY(5, "Rei!"),
    START(3, "Hajime!"),
    DECIDE(15, "L'arbitre doit rendre sa décision"),
    EXIT(15, "Les combattants peuvent quitter"),
    OVER(0, "Fin du match");
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
