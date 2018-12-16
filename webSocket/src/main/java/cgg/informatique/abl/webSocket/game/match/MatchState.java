package cgg.informatique.abl.webSocket.game.match;

import cgg.informatique.abl.webSocket.game.match.stateHandlers.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public enum MatchState {
    DEBUT("Les combattants se présentent à l'avant", DebutHandler.class,"Début"),
    CHOIX("Les combattants ont choisi leurs attaques", ChoixHandler.class, "Choix des attaques"),
    GAGNANT("Le gagnant est choisi", GagnantHandler.class, "Choix du gagnant"),
    PAUSE("Pause avant le prochain combat", PauseHandler.class, "Pause");

    private Class<? extends MatchStateHandler> handlerClass;
    private static final long SECONDS = 1000;
    private static final long ROUND_DURATION = 2 * SECONDS;
    private String transitionMessage;
    private String nom;

    MatchState(String transitionMessage, Class<? extends MatchStateHandler> handlerClass, String nom) {
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

    public void gererFinEtat(Match match) {
        MatchStateHandler handler = constructHandler(match);

        handler.handleTimeout();
    }

    public long getDuree() {
        return ROUND_DURATION;
    }

    public String getTransitionMessage() {
        return transitionMessage;
    }

    public String getMessage() {
        return this.nom;
    }
}
