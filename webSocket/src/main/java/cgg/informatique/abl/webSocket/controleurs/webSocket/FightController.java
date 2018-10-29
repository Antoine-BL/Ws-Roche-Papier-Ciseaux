package cgg.informatique.abl.webSocket.controleurs.webSocket;

import cgg.informatique.abl.webSocket.dto.Lobby;
import cgg.informatique.abl.webSocket.messaging.Heartbeat;
import cgg.informatique.abl.webSocket.messaging.commands.Commande;
import cgg.informatique.abl.webSocket.messaging.Courrier;
import cgg.informatique.abl.webSocket.messaging.Reponse;
import cgg.informatique.abl.webSocket.messaging.commands.LobbyCommandContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;

@RestController
public class FightController implements LobbyCommandContext {
    private static Lobby lobby;

    public FightController() {}

    @MessageMapping("/battle/chat")
    @SendTo("/topic/battle/chat")
    public Reponse fightChatMessage(Courrier message) throws Exception {
        return new Reponse(1L, message.getDe(), HtmlUtils.htmlEscape(message.getTexte()));
    }

    @MessageMapping("/battle/command")
    @SendTo("/topic/battle/command")
    public Reponse fightCommandMessage(Commande message) throws Exception {
        return message.execute(this);
    }

    public Lobby getLobby() {
        return lobby;
    }

    public void createLobby() {
        if (lobby == null) lobby = new Lobby();
    }

    @MessageMapping("/battle/heartbeat")
    public void heartBeat(Heartbeat message) throws Exception {
        lobby.getLobbyUserData(message.getDe()).sentHeartbeat();
    }
}
