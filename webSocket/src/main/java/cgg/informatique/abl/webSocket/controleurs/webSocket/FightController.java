package cgg.informatique.abl.webSocket.controleurs.webSocket;

import cgg.informatique.abl.webSocket.dao.CombatDao;
import cgg.informatique.abl.webSocket.dao.CompteDao;
import cgg.informatique.abl.webSocket.game.lobby.Lobby;
import cgg.informatique.abl.webSocket.game.lobby.LobbyContext;
import cgg.informatique.abl.webSocket.entites.Combat;
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
    private CombatDao combatDao;
    private CompteDao compteDao;

    public FightController(@Autowired SimpMessagingTemplate msg,
                           @Autowired CombatDao combatDao,
                           @Autowired CompteDao compteDao) {
        this.msg = msg;
        this.combatDao = combatDao;
        this.compteDao = compteDao;
        createLobby();
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

    public static Lobby getLobby() {
        if (lobby == null) throw new IllegalStateException("Aucun lobby d'ouvert");
        return lobby;
    }

    public void createLobby() {
        if (lobby == null){
            lobby = new Lobby(msg, this);
            new Thread(lobby).start();
        }
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

    @Override
    public void persistMatch(Combat combat) {
        combatDao.saveAndFlush(combat);
    }

    @Override
    public Compte getUser(String username) {
        return this.compteDao.findById(username).orElseThrow(() -> new IllegalStateException("Compte invalide"));
    }

    private Compte authToCompte(Authentication auth) {
        return (Compte)auth.getPrincipal();
    }
}
