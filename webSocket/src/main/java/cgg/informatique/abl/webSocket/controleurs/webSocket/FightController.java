package cgg.informatique.abl.webSocket.controleurs.webSocket;

import cgg.informatique.abl.webSocket.dto.lobby.Lobby;
import cgg.informatique.abl.webSocket.dto.lobby.LobbyContext;
import cgg.informatique.abl.webSocket.entites.Compte;
import cgg.informatique.abl.webSocket.messaging.commands.Commande;
import cgg.informatique.abl.webSocket.messaging.Courrier;
import cgg.informatique.abl.webSocket.messaging.Reponse;
import cgg.informatique.abl.webSocket.messaging.commands.LobbyCommandContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FightController implements LobbyCommandContext, LobbyContext {
    private static Lobby lobby;
    private SimpMessagingTemplate msg;

    public FightController(@Autowired SimpMessagingTemplate msg) {
        this.msg = msg;
    }

    @MessageMapping("/battle/chat")
    @SendTo("/topic/battle/chat")
    public Reponse fightChatMessage(Courrier message, Authentication auth) throws Exception {
        return  new Reponse(1L, authToCompte(auth), message.getTexte());
    }

    @MessageMapping("/battle/command")
    public void fightCommandMessage(Commande message, Authentication auth) throws Exception {
        message.setDe(authToCompte(auth));
        message.execute(this);
    }

    public Lobby getLobby() {
        if (lobby == null) throw new IllegalStateException("Aucun lobby d'ouvert");
        return lobby;
    }

    public void createLobby() {
        if (lobby == null) lobby = new Lobby(msg, this);
        else throw new IllegalStateException("Il existe déjà un lobby!");
    }

    @Override
    public SimpMessagingTemplate getMessaging() {
        return this.msg;
    }

    @MessageMapping("/battle/heartbeat")
    public void heartBeat(Message message, Authentication auth) throws Exception {
        lobby.getLobbyUserData(authToCompte(auth)).sentHeartbeat();
    }

    @Override
    public void lobbyClosed() {
        lobby = null;
    }

    private Compte authToCompte(Authentication auth) {
        return (Compte)auth.getPrincipal();
    }
}
