package cgg.informatique.abl.webSocket.game.match;

import cgg.informatique.abl.webSocket.game.match.stateHandlers.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public enum MatchState {
    WAITING(15, "Que les combattants se présentent à l'avant", WaitingHandler.class, "Attente"),
    READY(15, "Rei!", ReadyHandler.class, "Prêt"),
    START(3, "Hajime!", StartHandler.class, "Combat!"),
    DECIDE(15, "L'arbitre doit décider d'un verdict", DecideHandler.class, "Décision"),
    EXIT(20, "Les combattants peuvent quitter", ExitHandler.class, "Quitter"),
    OVER(0, "Fin du match. Les joueurs sont retournés à leurs positions", OverHandler.class, "");
    private Class<? extends MatchStateHandler> handlerClass;
    private static final long SECOND = 1000;
    private long duration;
    private String transitionMessage;
    private String nom;

    MatchState(int nbSeconds, String transitionMessage, Class<? extends MatchStateHandler> handlerClass, String nom) {
        duration = nbSeconds * SECOND;
        this.handlerClass = handlerClass;
        this.transitionMessage = transitionMessage;
        this.nom = nom;
    }

    private MatchStateHandler constructHandler(Match match) {
        Constructor<? extends MatchStateHandler> constructor = null;
        try {
            constructor = handlerClass.getConstructor(Match.class);

            return constructor.newInstance(match);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Handler didn't have constructor with parameter Lobby");
        }
    }

    public void handleTimeout(Match match) {
        MatchStateHandler handler = constructHandler(match);

        handler.handleTimeout();
    }

    public long getDuration() {
        return duration;
    }

    public String getTransitionMessage() {
        return transitionMessage;
    }

    public String getMessage() {
        return this.nom;
    }

    public void handleStateChanged(Match match) {
        constructHandler(match).handleStateChanged();
    }
}
