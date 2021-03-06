package cgg.informatique.abl.webSocket.configurations.stomp;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

@Configuration
public class WebSocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {
    @Override
    protected void configureInbound(
            MessageSecurityMetadataSourceRegistry messages) {
        messages.simpMessageDestMatchers("/app/public/chat")
                .hasAnyAuthority("Venerable", "Sensei", "Ancien")
                .simpSubscribeDestMatchers("/topic/chat/prive", "/topic/battle**").authenticated()
                .simpMessageDestMatchers("/topic/chat/prive", "/topic/battle**").authenticated()
                .anyMessage().permitAll();
    }

    @Override
    protected boolean sameOriginDisabled() {
        return true;
    }
}
