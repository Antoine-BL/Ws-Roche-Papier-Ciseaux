package cgg.informatique.abl.webSocket.controleurs.webSocket;

import cgg.informatique.abl.webSocket.messaging.Courrier;
import cgg.informatique.abl.webSocket.messaging.Reponse;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;

@RestController
public class ChatController {
    @MessageMapping("/public/chat")
    @SendTo("/topic/public/chat")
    public Reponse publicMessage(Courrier message) throws Exception {
        return new Reponse(1L, message.getDe(),HtmlUtils.htmlEscape(message.getTexte()));
    }

    @MessageMapping("/private/chat")
    @SendTo("/topic/private/chat")
    public Reponse privateMessage(Courrier message) throws Exception {
        return new Reponse(1L, message.getDe(),HtmlUtils.htmlEscape(message.getTexte()));
    }
}
