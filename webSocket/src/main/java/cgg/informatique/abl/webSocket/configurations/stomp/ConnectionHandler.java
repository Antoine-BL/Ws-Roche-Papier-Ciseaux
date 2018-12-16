package cgg.informatique.abl.webSocket.configurations.stomp;

import cgg.informatique.abl.webSocket.controleurs.webSocket.FightController;
import cgg.informatique.abl.webSocket.entites.Compte;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class ConnectionHandler implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);
        StompCommand command = headerAccessor.getCommand();

        if (command == StompCommand.CONNECT) {
            handleConnect(headerAccessor);
        } else if (command == StompCommand.DISCONNECT) {
            handleDisconnect(headerAccessor);
        }

        return message;
    }

    private void handleDisconnect(StompHeaderAccessor headerAccessor) {
        if (isNotAuthenticated(headerAccessor)) return;

        Compte user = getUserFrom(headerAccessor);

        FightController.getLobby().quitter(FightController.getLobby().getLobbyUserData(user));
    }

    private void handleConnect(StompHeaderAccessor headerAccessor) {
        if (isNotAuthenticated(headerAccessor)) return;

        Compte user = getUserFrom(headerAccessor);

        try {
            FightController.getLobby().getLobbyUserData(user);
        } catch (IllegalArgumentException e) {
            FightController.getLobby().connect(user);
        }
    }

    private Compte getUserFrom(StompHeaderAccessor headerAccessor) {
        return (Compte)((UsernamePasswordAuthenticationToken)headerAccessor.getUser()).getPrincipal();
    }

    private boolean isNotAuthenticated(StompHeaderAccessor headerAccessor) {
        return headerAccessor.getUser() == null;
    }
}