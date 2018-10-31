package cgg.informatique.abl.webSocket.configurations.stomp;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

@Configuration
public class WebSocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {
    @Override
    protected void configureInbound(
            MessageSecurityMetadataSourceRegistry messages) {
        messages.simpMessageDestMatchers("/app/chat/public")
                .hasAnyAuthority("VENERABLE", "SENSEI", "ANCIEN")
                .simpDestMatchers("**chat/prive", "**battle**")
                .authenticated();
    }

    @Override
    protected boolean sameOriginDisabled() {
        return true;
    }
}
