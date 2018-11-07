package cgg.informatique.abl.webSocket.dto.lobby;

import cgg.informatique.abl.webSocket.dto.match.Match;
import cgg.informatique.abl.webSocket.dto.match.MatchHandler;
import cgg.informatique.abl.webSocket.dto.match.SerializableMatch;
import cgg.informatique.abl.webSocket.entites.*;
import cgg.informatique.abl.webSocket.messaging.DonneesReponseCommande;
import cgg.informatique.abl.webSocket.messaging.Reponse;
import cgg.informatique.abl.webSocket.messaging.ReponseCommande;
import cgg.informatique.abl.webSocket.messaging.commands.TypeCommande;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.*;

public class Lobby implements Runnable, MatchHandler, SerializableLobby {
    private static final int INDEX_ROUGE = 0;
    private static final int INDEX_BLANC = 1;
    private static final String OUTPUT_TOPIC = "/topic/battle/lobby";
    private static final String OUTPUT_TOPIC_COMMAND = "/topic/battle/command";
    public static final Compte COMPTE_LOBBY = Compte.Builder()
            .avecCourriel("lobby@server.ca")
            .avecMotDePasse("")
            .avecAlias("LobbyBot")
            .avecRole(new Role(""))
            .avecGroupe(new Groupe(""))
            .avecAvatar(new Avatar(3L))
            .build();

    private static final int ONE_SECOND = 1000;

    private static final int TICK_RATE_HZ = 1;
    private static final int TICK_DURATION = ONE_SECOND / TICK_RATE_HZ;

    private static final int LOBBY_TIMEOUT = 90 * ONE_SECOND;
    private static final int WAIT_MESSAGE_INTERVAL = 10* ONE_SECOND;
    private static final String WAIT_MESSAGE_TMP = "En attente de joueurs (%ds restantes)";

    private Thread lobbyThread;

    private List<LobbyUserData> users;
    private RoleColl spectateurs;
    private RoleColl combattants;
    private LobbyUserData rouge;
    private LobbyUserData blanc;
    private LobbyUserData arbitre;

    private volatile LobbyState lobbyState;
    private Match matchInProgress;
    private final SimpMessagingTemplate messaging;
    private LobbyContext lobbyContext;

    public Lobby(SimpMessagingTemplate messaging, LobbyContext ctxt) {
        this.messaging = messaging;
        this.lobbyContext = ctxt;

        lobbyState = LobbyState.CLOSED;
        users = Collections.synchronizedList(new ArrayList<>(27));
        spectateurs = new RoleColl(LobbyRole.SPECTATEUR);
        combattants = new RoleColl(LobbyRole.COMBATTANT);
    }

    @Override
    public void run() {
        lobbyThread = Thread.currentThread();

        try {
            lobbyState = LobbyState.STANDBY;
            send("Lobby ouvert!");
            waitForPlayers();
            mainLoop();
            send("Lobby closed");
            lobbyContext.lobbyClosed();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void mainLoop() throws InterruptedException {
        long tickStart;
        long processingTime = 0;
        while (lobbyState == LobbyState.ACTIVE) {
            Thread.sleep(Math.max(TICK_DURATION - processingTime, 0));
            tickStart = System.currentTimeMillis();

            System.out.println("tick!");

            purgeInactiveUsers();
            
            if (isEmpty()) becomeInactive();
            if (matchInProgress != null) matchInProgress.tick();
            processingTime = System.currentTimeMillis() - tickStart;
        }
    }

    private void purgeInactiveUsers() {
        for (int i = 0; i < users.size(); i++) {
            LobbyUserData user = users.get(i);

            if (user.isTimedOut()) {
                removeFromLobby(user);
                quitter(user);
                System.out.println(user.getUser().getAlias() + " kicked due to inactivity");
            }

            if (user.isInactive() && !user.isWarned()) {
                user.setWarned(true);
                send(user.getUser().getAlias() + " est inactif. Il sera bientôt sorti du lobby.");
                System.out.println(user.getUser().getAlias() + " inactive");
            }
        }
    }

    private void waitForPlayers() {
        try {
            for (int i = 0; lobbyState == LobbyState.STANDBY; i++) {
                int remainingS = (LOBBY_TIMEOUT - WAIT_MESSAGE_INTERVAL * i)/ ONE_SECOND;
                if (remainingS <= 0) {
                    send("Fermeture du lobby dû à l'inactivité");


                    lobbyState = LobbyState.CLOSED;
                }

                send(String.format(WAIT_MESSAGE_TMP, remainingS));
                Thread.sleep(WAIT_MESSAGE_INTERVAL);
            }
        } catch (InterruptedException e) {
            send("Lobby actif");
            lobbyState = LobbyState.ACTIVE;
        }
    }

    private void becomeInactive() {
        send("Lobby inactif");
        lobbyState = LobbyState.STANDBY;
        waitForPlayers();
    }

    public void send(String message) {
        Reponse reponse = new Reponse(1L, COMPTE_LOBBY, message);
        synchronized (messaging) {
            messaging.convertAndSend(OUTPUT_TOPIC, reponse);
        }
    }

    public void sendData(String message, DonneesReponseCommande donnees) {
        ReponseCommande reponse = new ReponseCommande(COMPTE_LOBBY, message, donnees);
        synchronized (messaging) {
            messaging.convertAndSend(OUTPUT_TOPIC, reponse);
        }
    }

    private void sendData(DonneesReponseCommande donnees) {
        sendData(null, donnees);
    }

    public void connect(Compte u) {
        if (lobbyState == LobbyState.STANDBY) this.lobbyThread.interrupt();
        if (users.stream().anyMatch(u::equals)) throw  new IllegalArgumentException("utilisateur déjà présent");

        LobbyUserData userData = new LobbyUserData(u, this);

        this.users.add(userData);
        userData.becomeRole(new LobbyPosition(LobbyRole.SPECTATEUR));

        sendData("Bienvenue au lobby " + u.getAlias(), new DonneesReponseCommande(TypeCommande.JOINDRE, this.asSerializable()));
    }

    public void posChanged(LobbyUserData user, LobbyPosition newPos, LobbyPosition oldPos) {
        sendData(String.format("%s a maintenant le role de %s", user.getAlias(), user.getRoleCombat()),
                new DonneesReponseCommande(TypeCommande.ROLE, user, newPos, oldPos));
    }

    public void quitter(LobbyUserData u) {
        LobbyPosition pos = removeFromLobby(u);
        if (pos != null) {
            sendData(u.getUser().getAlias() + " a quitté",
                    new DonneesReponseCommande(TypeCommande.QUITTER, u, pos));
            sendDataTo(u.getUser().getAlias() + " a quitté",
                    new DonneesReponseCommande(TypeCommande.QUITTER, u, pos));
        }
    }

    private void sendDataTo(String message, DonneesReponseCommande donneesReponseCommande) {
        this.messaging.convertAndSendToUser(
                donneesReponseCommande.getDe().getCourriel(),
                OUTPUT_TOPIC_COMMAND,
                new ReponseCommande(COMPTE_LOBBY, message, donneesReponseCommande));
    }

    private synchronized LobbyPosition removeFromLobby(LobbyUserData utilisateur) {
        LobbyPosition pos = utilisateur.leaveCurrentRole();

        users.remove(utilisateur);

        return pos;
    }

    public LobbyUserData getLobbyUserData(Compte compte) throws IllegalArgumentException {
        return users.stream()
                .filter((LobbyUserData lud) -> lud.getUser().equals(compte))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur " + compte.getAlias() + " n'existe pas dans ce lobby"));
    }

    public void startMatch() {
        ensureReadyForMatch();

        Match match = new Match(this.arbitre, this);
        match.choisirParticipantsParmi(combattants);

        send(String.format("Combattants choisis: %s en blanc vs %s en rouge", match.getBlanc().getCourriel(), match.getRouge().getCourriel()));
        matchInProgress = match;
    }

    private void ensureReadyForMatch() {
        if (matchInProgress != null) throw new IllegalStateException("Un match est déjà en cours.");

        if (combattants.size() < 2) throw new IllegalStateException("Il faut un minimum de 2 combattants avant de débuter un combat");
        if (arbitre == null) throw new IllegalStateException("L'arbitre doit être présent");
    }

    @Override
    public void matchEnded(Combat combat) {
        send("fin du match");
        matchInProgress = null;
        this.lobbyContext.persistMatch(combat);
    }

    public Match getCurrentMatch() {
        return matchInProgress;
    }

    private boolean isEmpty() {
        return users.isEmpty();
    }

    @Override
    public LobbyUserData[] getSpectateurs() {
        return this.spectateurs.getContents();
    }

    @Override
    public LobbyUserData[] getCombattants() {
        return this.combattants.getContents();
    }

    public RoleColl getCombattantsColl() {
        return this.combattants;
    }

    public RoleColl getSpectateursColl() {
        return this.spectateurs;
    }

    @Override
    public LobbyUserData getBlanc() {
        return blanc;
    }

    @Override
    public LobbyUserData getRouge() {
        return rouge;
    }

    @Override
    public LobbyUserData getArbitre() {
        return arbitre;
    }

    @Override
    public SerializableMatch getMatch() {
        return matchInProgress;
    }

    @Override
    public SerializableLobby asSerializable() {
        return this;
    }

    public void setBlanc(LobbyUserData o) {
        blanc = o;
    }

    public void setRouge(LobbyUserData rouge) {
        this.rouge = rouge;
    }

    public void setArbitre(LobbyUserData arbitre) {
        this.arbitre = arbitre;
    }
}
