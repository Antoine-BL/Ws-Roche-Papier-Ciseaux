package cgg.informatique.abl.webSocket.configurations;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.security.Principal;
import java.util.Arrays;
import java.util.Collection;

class TopicChannelInterceptor implements ChannelInterceptor {
    private Collection<String> authPourEcrire = Arrays.asList("ROLE_Sensei",
            "ROLE_Venerable");
    private UserDetailsService userDetailsService;

    public TopicChannelInterceptor(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);

        Principal userPrincipal = headerAccessor.getUser();
        StompCommand commande = headerAccessor.getCommand();

        if (StompCommand.SUBSCRIBE.equals(commande)) {
            if (!validerAcces(userPrincipal, headerAccessor.getDestination())) {
                throw new IllegalArgumentException("Accès de lecture refusé pour ce sujet");
            }
        } else if (StompCommand.SEND.equals(commande)) {
            if (!validerPermissionEnvoi(userPrincipal, headerAccessor.getDestination()))
                throw new IllegalArgumentException("Accès d'écriture refusé pour ce sujet");
        }

        return message;
    }

    private boolean validerAcces(Principal utilisateur, String destination) {
        if (!destinationEstPrivee(destination)) return true;

        return utilisateur != null;
    }

    private boolean validerPermissionEnvoi(Principal utilisateur, String destination){
        if (!destinationEstPrivee(destination)) return true;

        if (utilisateur == null) return false;

        return estAutorise(userDetailsService.loadUserByUsername(utilisateur.getName()));
    }

    private boolean estAutorise(UserDetails utilisateur) {
        return utilisateur.getAuthorities()
                .stream()
                .anyMatch(auth ->
                        authPourEcrire.stream()
                                .anyMatch(
                                        auth2 ->
                                                auth2.equals((auth).getAuthority()))
                );
    }

    private boolean destinationEstPrivee(String destination) {
        if (destination == null) return true;
        return destination.matches(".*/private/.*");
    }
}
