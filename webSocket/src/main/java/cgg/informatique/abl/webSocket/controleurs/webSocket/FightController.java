package cgg.informatique.abl.webSocket.controleurs.webSocket;

import cgg.informatique.abl.webSocket.configurations.UserDetailsImpl;
import cgg.informatique.abl.webSocket.dto.Lobby;
import cgg.informatique.abl.webSocket.entites.Compte;
import cgg.informatique.abl.webSocket.messaging.commands.Commande;
import cgg.informatique.abl.webSocket.messaging.Courrier;
import cgg.informatique.abl.webSocket.messaging.Reponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;

@RestController
public class FightController {
    private static Lobby lobby;
    private UserDetailsService userDetailsService;

    public FightController(@Autowired @Qualifier("compte") UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @MessageMapping("/battle/chat")
    @SendTo("/topic/battle/chat")
    public Reponse fightChatMessage(Courrier message) throws Exception {
        return new Reponse(1L, message.getDe(), HtmlUtils.htmlEscape(message.getTexte()));
    }

    @MessageMapping("/battle/command")
    @SendTo("/topic/battle/command")
    public Reponse fightCommandMessage(Commande message) throws Exception {
//        if (auth != null && message.getDe() == null) {
//            Compte utilisateur = ((UserDetailsImpl)userDetailsService.loadUserByUsername(auth.getName())).getCompte();
//
//            message.setDe(utilisateur);
//        }

        return message.execute(this);
    }

    public Lobby getLobby() {
        if (lobby == null) {
            lobby = new Lobby();
        }

        return lobby;
    }
}
