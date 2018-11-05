package cgg.informatique.abl.webSocket.dto.match;

import cgg.informatique.abl.webSocket.dto.lobby.Lobby;
import cgg.informatique.abl.webSocket.dto.lobby.stateHandlers.LobbyRoleHandler;
import cgg.informatique.abl.webSocket.dto.match.stateHandlers.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public enum MatchState {
    WAITING(10000000, "Que les combattants se présentent à l'avant", WaitingHandler.class),
    READY(60, "Rei!", ReadyHandler.class),
    START(10, "Hajime!", StartHandler.class),
    DECIDE(20, "L'arbitre doit décider d'un verdict", DecideHandler.class),
    EXIT(2, "Les combattants peuvent quitter", ExitHandler.class),
    OVER(0, "Fin du match. Les joueurs sont retournés à leurs positions", OverHandler.class);
    private Class<? extends MatchStateHandler> handlerClass;
    private static final long SECOND = 1000;
    private long duration;
    private String transitionMessage;

    MatchState(int nbSeconds, String transitionMessage, Class<? extends MatchStateHandler> handlerClass) {
        duration = nbSeconds * SECOND;
        this.handlerClass = handlerClass;
        this.transitionMessage = transitionMessage;
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
}
