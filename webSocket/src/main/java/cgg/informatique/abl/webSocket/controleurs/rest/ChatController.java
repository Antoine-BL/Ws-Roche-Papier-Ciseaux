package cgg.informatique.abl.webSocket.controleurs.rest;

import cgg.informatique.abl.webSocket.dto.Courrier;
import cgg.informatique.abl.webSocket.dto.Message;
import cgg.informatique.abl.webSocket.dto.Reponse;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;

@RestController
public class ChatController {
    @MessageMapping("/public/chat")
    @SendTo("/topic/public/chat")
    public Reponse publicMessage(Message message) throws Exception {
        return new Reponse(1L, message.getDe(),HtmlUtils.htmlEscape(message.getTexte()));
    }

    @MessageMapping("/private/chat")
    @SendTo("/topic/private/chat")
    public Reponse privateMessage(Message message) throws Exception {
        return new Reponse(1L, message.getDe(),HtmlUtils.htmlEscape(message.getTexte()));
    }
}
