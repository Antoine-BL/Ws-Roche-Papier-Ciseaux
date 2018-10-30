package cgg.informatique.abl.webSocket.dto;

import cgg.informatique.abl.webSocket.configurations.stomp.StompSessionHandlerImpl;
import cgg.informatique.abl.webSocket.messaging.commands.LobbyMessage;
import cgg.informatique.abl.webSocket.messaging.commands.TypeCommande;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Predicate;

public class Lobby implements Runnable, MatchHandler{
    private static final String URL = "ws://localhost:8100/webSocket";
    private static final String OUTPUT_TOPIC = "/app/battle/command";

    private static final int ONE_SECOND = 1000;

    private static final int TICK_RATE_HZ = 1;
    private static final int TICK_DURATION = ONE_SECOND / TICK_RATE_HZ;

    private static final int LOBBY_TIMEOUT = 90 * ONE_SECOND;
    private static final int WAIT_MESSAGE_INTERVAL = 10* ONE_SECOND;
    private static final String WAIT_MESSAGE_TMP = "En attente de joueurs (%ds restantes)";

    private Thread lobbyThread;

    private List<LobbyUserData> users;
    private List<LobbyUserData> spectateurs;
    private List<LobbyUserData> combattants;
    private LobbyUserData rouge;
    private LobbyUserData blanc;
    private LobbyUserData arbitre;

    private WebSocketStompClient stompClient;
    private StompSession stompSession;

    private volatile LobbyState lobbyState;
    private Match matchInProgress;

    public Lobby() {
        lobbyState = LobbyState.CLOSED;

        users = Collections.synchronizedList(new ArrayList<>(27));
        spectateurs = Collections.synchronizedList(new ArrayList<>(12));
        combattants = Collections.synchronizedList(new ArrayList<>(12));

        initStompClient();
    }

    private void initStompClient() {
        WebSocketClient simpleWebSocketClient = new StandardWebSocketClient();
        List<Transport> transports = new ArrayList<>();

        transports.add(new WebSocketTransport(simpleWebSocketClient));
        SockJsClient sockJsClient = new SockJsClient(transports);

        stompClient = new WebSocketStompClient(sockJsClient);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
    }

    @Override
    public void run() {
        lobbyThread = Thread.currentThread();

        try {
            stompSession = stompClient.connect(URL, new StompSessionHandlerImpl()).get();
            lobbyState = LobbyState.STANDBY;
            send("Lobby ouvert!");
            waitForPlayers();
            mainLoop();
            send("Lobby closed");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void mainLoop() throws InterruptedException {
        while (lobbyState == LobbyState.ACTIVE) {
            Thread.sleep(TICK_DURATION);

            System.out.println("tick!");

            purgeInactiveUsers();
            
            if (isEmpty()) becomeInactive();
            if (matchInProgress != null) matchInProgress.tick();
        }
    }

    private void becomeInactive() {
        send("Lobby inactif");
        lobbyState = LobbyState.STANDBY;
        waitForPlayers();
    }

    private void purgeInactiveUsers() {
        for (int i = 0; i < users.size(); i++) {
            LobbyUserData user = users.get(i);

            if (user.isTimedOut()) {
                removeFromLobby(user);
                send(user.getUser().getAlias() + " kicked due to inactivity");
                System.out.println(user.getUser().getAlias() + " kicked due to inactivity");
            }

            if (user.isInactive() && !user.isWarned()) {
                user.setWarned(true);
                send(user.getUser().getAlias() + " est inactif. Il sera bientôt sorti du lobby.");
                System.out.println(user.getUser().getAlias() + " inactive");
            }
        }
    }

    private void send(String message) {
        LobbyMessage lobbyMessage = new LobbyMessage(Arrays.asList(message));
        lobbyMessage.setTypeCommande(TypeCommande.LOBBYMESSAGE);

        synchronized (stompSession) {
            stompSession.send(OUTPUT_TOPIC, lobbyMessage);
        }
    }

    private void waitForPlayers() {
        try {
            for (int i = 0; lobbyState == LobbyState.STANDBY; i++) {
                int remainingS = (LOBBY_TIMEOUT - WAIT_MESSAGE_INTERVAL * i)/ ONE_SECOND;
                if (remainingS <= 0) {
                    send("Lobby timeout exceeded. Shutting down.");

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

    private boolean isEmpty() {
        return users.isEmpty();
    }

    public void connect(SanitaryCompte u) {
        if (lobbyState == LobbyState.STANDBY) this.lobbyThread.interrupt();
        if (users.stream().anyMatch(isPresent(u))) throw  new IllegalArgumentException("utilisateur déjà présemt");

        LobbyUserData userData = new LobbyUserData(u, LobbyRole.SPECTATEUR);

        this.users.add(userData);
        this.spectateurs.add(userData);

        send("Bienvenue au lobby " + u.getAlias());
    }

    private void removeFromCurrentRole(LobbyUserData u) {
        switch (u.getRole()) {
            case BLANC:
                blanc = null;
                break;
            case ROUGE:
                rouge = null;
                break;
            case SPECTATEUR:
                removeFromList(u, spectateurs);
                break;
            case COMBATTANT:
                removeFromList(u,combattants);
                break;
            case ARBITRE:
                arbitre = null;
                break;
        }
    }

    private void switchToRole(LobbyUserData u, LobbyRole role) {
        switch (role) {
            case BLANC:
                if (blanc != null)
                    throw new IllegalArgumentException("il y a déjà un combattant blanc");
                blanc = u;
                break;
            case ROUGE:
                if (rouge != null)
                    throw new IllegalArgumentException("Il y a déjà un combattant rouge");
                rouge = u;
                break;
            case ARBITRE:
                if (arbitre != null)
                    throw new IllegalArgumentException("Il y a déjà un arbitre");
                arbitre = u;
                break;
            case SPECTATEUR:
                if (spectateurs.size() >= 12)
                    throw new IllegalArgumentException("Nombre maximal de spectateurs déjà atteint");
                spectateurs.add(u);
                break;
            case COMBATTANT:
                if (combattants.size() >= 12)
                    throw new IllegalArgumentException("Nombre maximal de combattants déjà atteint");
                combattants.add(u);
                break;
        }

        u.setRole(role);
    }

    public void devenirRole(LobbyUserData u, LobbyRole role) {
        removeFromCurrentRole(u);
        switchToRole(u, role);
        send (u.getUser().getAlias() + " a maintenant le role de: " + role);
    }

    public void quitter(LobbyUserData u) {
        send(u.getUser().getAlias() + " a quitté");
        removeFromLobby(u);
    }

    private Predicate<LobbyUserData> isPresent(LobbyUserData utilisateur) {
        return (cpt) -> cpt.equals(utilisateur);
    }

    private Predicate<LobbyUserData> isPresent(SanitaryCompte cpt) {
        return (lud) -> cpt.getUsername().equals(lud.getUser().getUsername());
    }


    private synchronized void removeFromList(LobbyUserData utilisateur, List<LobbyUserData> liste) {
        liste.removeIf(isPresent(utilisateur));
    }

    private synchronized void removeFromLobby(LobbyUserData utilisateur) {
        removeFromCurrentRole(utilisateur);
        removeFromList(utilisateur, users);
    }

    public LobbyUserData getLobbyUserData(SanitaryCompte compte) throws IllegalArgumentException {
        return users.stream()
                .filter((LobbyUserData lud) -> lud.getUser().getUsername().equals(compte.getUsername()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No such user"));
    }

    public void startMatch() {
        ensureReadyForMatch();

        matchInProgress = new Match(arbitre, rouge, blanc, this);
    }

    private void ensureReadyForMatch() {
        if (matchInProgress != null) throw new IllegalStateException("Un match est déjà en cours.");

        if (rouge == null) throw new IllegalStateException("Le combattant en rouge doit être présent");
        if (blanc == null) throw new IllegalStateException("Le combattant en blanc doit être présent");
        if (arbitre == null) throw new IllegalStateException("L'arbitre doit être présent");
    }

    @Override
    public void matchEnded() {
        send("fin du match");
        matchInProgress = null;
        blanc.setRole(LobbyRole.COMBATTANT);
        rouge.setRole(LobbyRole.COMBATTANT);
    }

    @Override
    public void sendMessage(String transitionMessage) {
        send(transitionMessage);
    }

    @Override
    public void sendRound(Attack blancAttack, Attack rougeAttack) {
        send("Attaque du participant en blanc: " + blancAttack);
        send("Attaque du participant en rouge: " + rougeAttack);
    }

    public Match getCurrentMatch() {
        return matchInProgress;
    }
}
