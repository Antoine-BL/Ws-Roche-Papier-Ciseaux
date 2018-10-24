package cgg.informatique.abl.webSocket.configurations;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;

import java.security.Principal;

class TopicChannelInterceptor implements ChannelInterceptor {
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.SUBSCRIBE.equals(headerAccessor.getCommand())){
            Principal userPrincipal = headerAccessor.getUser();
            if (!validerAcces(userPrincipal, headerAccessor.getDestination())) {
                throw new IllegalArgumentException("Accès refusé pour ce sujet");
            }
        }

        return message;
    }

    private boolean validerAcces(Principal utilisateur, String destination) {
        if (destination == null) return true;
        boolean destPublique = destination.matches(".*/public/.*");

        if (destPublique) return true;

        return utilisateur != null;
    }
}
