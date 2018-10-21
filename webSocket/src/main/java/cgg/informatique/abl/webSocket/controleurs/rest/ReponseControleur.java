package cgg.informatique.abl.webSocket.controleurs.rest;

import cgg.informatique.abl.webSocket.dto.Message;
import cgg.informatique.abl.webSocket.dto.Reponse;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReponseControleur {

    static long id = 1;

    @CrossOrigin()
    @MessageMapping("/messagepublique")
    @SendTo("/sujet/reponsepublique")
    public Reponse reponse(Message message) {
        return new Reponse( id++, message.getDe(), message.getTexte());
    }
}
