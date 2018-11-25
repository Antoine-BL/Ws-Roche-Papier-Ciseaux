package cgg.informatique.abl.webSocket.game.lobby;

import cgg.informatique.abl.webSocket.game.lobby.stateHandlers.*;
import cgg.informatique.abl.webSocket.entites.Compte;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public enum LobbyRole {
    COMBATTANT(CombattantHandler.class),
    SPECTATEUR(SpectateurHandler.class),
    ROUGE(RougeHandler.class),
    BLANC(BlancHandler.class),
    ARBITRE(ArbitreHandler.class),
    AILLEURS(null);
    private Class<? extends LobbyRoleHandler> handlerClass;

    LobbyRole(Class<? extends LobbyRoleHandler> handlerClass) {
        this.handlerClass = handlerClass;
    }

    private LobbyRoleHandler constructHandler(Lobby lobby) {
        Constructor<? extends LobbyRoleHandler> constructor = null;
        try {
            constructor = handlerClass.getConstructor(Lobby.class);

            return constructor.newInstance(lobby);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Handler didn't have constructor with parameter Lobby");
        }
    }

    public LobbyPosition leaveRole(Lobby lobby, Compte u) {
        return leaveRole(lobby.getLobbyUserData(u));
    }

    public LobbyPosition leaveRole(LobbyUserData u) {
        Lobby lobby = u.getLobby();

        LobbyRoleHandler handler = constructHandler(lobby);

        return handler.removeFromRole(u);
    }

    public void becomeRole(Lobby lobby, Compte user) {
        becomeRole(lobby.getLobbyUserData(user), new LobbyPosition(this));
    }

    public void becomeRole(Lobby lobby, Compte user, int position) {
        becomeRole(lobby.getLobbyUserData(user), new LobbyPosition(this, position));
    }


    public LobbyPosition becomeRole(LobbyUserData u, LobbyPosition position) {
        Lobby lobby = u.getLobby();

        LobbyRoleHandler handler = constructHandler(lobby);

        return handler.addToRole(u, position.getPosition());
    }

    public void checkAvailable(Lobby lobby, Compte u) {
        checkAvailable(lobby.getLobbyUserData(u), null);
    }

    public void checkAvailable(LobbyUserData u, Integer position) {
        Lobby lobby = u.getLobby();

        LobbyRoleHandler handler = constructHandler(lobby);

        handler.checkAvailable(u, position);
    }
}
