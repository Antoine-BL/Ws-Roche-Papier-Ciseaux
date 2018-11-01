package cgg.informatique.abl.webSocket.dto.lobby;

import cgg.informatique.abl.webSocket.dto.match.Attack;
import cgg.informatique.abl.webSocket.dto.match.Match;
import cgg.informatique.abl.webSocket.dto.match.MatchHandler;
import cgg.informatique.abl.webSocket.dto.SanitaryCompte;
import cgg.informatique.abl.webSocket.messaging.DonneesReponse;
import cgg.informatique.abl.webSocket.messaging.Reponse;
import cgg.informatique.abl.webSocket.messaging.ReponseCommande;
import cgg.informatique.abl.webSocket.messaging.commands.Commande;
import cgg.informatique.abl.webSocket.messaging.commands.TypeCommande;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public class Lobby implements Runnable, MatchHandler {
    private static final String URL = "ws://localhost:8100/webSocket";
    private static final String OUTPUT_TOPIC = "/topic/battle/command";

    private static final int ONE_SECOND = 1000;

    private static final int TICK_RATE_HZ = 1;
    private static final int TICK_DURATION = ONE_SECOND / TICK_RATE_HZ;

    private static final int LOBBY_TIMEOUT = 5 * ONE_SECOND;
    private static final int WAIT_MESSAGE_INTERVAL = 10* ONE_SECOND;
    private static final String WAIT_MESSAGE_TMP = "En attente de joueurs (%ds restantes)";

    private Thread lobbyThread;

    private List<LobbyUserData> users;
    private LobbyUserData[] spectateurs;
    private LobbyUserData[] combattants;
    private LobbyUserData rouge;
    private LobbyUserData blanc;
    private LobbyUserData arbitre;

    private volatile LobbyState lobbyState;
    private Match matchInProgress;
    private final SimpMessagingTemplate messaging;

    public Lobby(SimpMessagingTemplate messaging, LobbyContext ctxt) {
        this.messaging = messaging;
        lobbyState = LobbyState.CLOSED;

        users = Collections.synchronizedList(new ArrayList<>(27));
        spectateurs = new LobbyUserData[12];
        combattants = new LobbyUserData[12];
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
        } catch (InterruptedException e) {
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

    private void send(String message) {
        Reponse reponse = new Reponse(1L, Commande.COMPTE_SERVEUR, message);
        synchronized (messaging) {
            messaging.convertAndSend(OUTPUT_TOPIC, reponse);
        }
    }

    private void sendData(String message, DonneesReponse donnees) {
        ReponseCommande reponse = new ReponseCommande(message, donnees);
        synchronized (messaging) {
            messaging.convertAndSend(OUTPUT_TOPIC, reponse);
        }
    }

    private void sendData(DonneesReponse donnees) {
        ReponseCommande reponse = new ReponseCommande(donnees);
        synchronized (messaging) {
            messaging.convertAndSend(OUTPUT_TOPIC, reponse);
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

    private boolean isEmpty() {
        return users.isEmpty();
    }

    public void connect(SanitaryCompte u) {
        if (lobbyState == LobbyState.STANDBY) this.lobbyThread.interrupt();
        if (users.stream().anyMatch(isPresent(u))) throw  new IllegalArgumentException("utilisateur déjà présent");

        LobbyUserData userData = new LobbyUserData(u, LobbyRole.SPECTATEUR);

        this.users.add(userData);
        int position = addTo(spectateurs, userData);

        sendData("Bienvenue au lobby " + u.getAlias(), new DonneesReponse(TypeCommande.ROLE, userData,
                new LobbyPosition(LobbyRole.SPECTATEUR, position)));
    }


    private synchronized LobbyPosition removeFromCurrentRole(LobbyUserData u) {
        switch (u.getRoleCombat()) {
            case BLANC:
                blanc = null;
                return new LobbyPosition(LobbyRole.BLANC);
            case ROUGE:
                rouge = null;
                return new LobbyPosition(LobbyRole.ROUGE);
            case ARBITRE:
                arbitre = null;
                return new LobbyPosition(LobbyRole.ARBITRE);
            case SPECTATEUR:
                return removeFromList(u, spectateurs);
            case COMBATTANT:
                return removeFromList(u, combattants);
            default:
                return null;
        }
    }

    private synchronized LobbyPosition switchToRole(LobbyUserData u, LobbyPosition destination) {
        switch (destination.getRole()) {
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
                addPlayer(u, destination, spectateurs);
            case COMBATTANT:
                addPlayer(u, destination, combattants);
        }

        return destination;
    }

    private void addPlayer(LobbyUserData u, LobbyPosition destination, LobbyUserData[] arr) {
        try {
            if (destination.getPosition() == null) {
                destination.setPosition(addTo(arr, u));
            } else {
                addTo(arr, u, destination.getPosition());
            }
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Nombre maximal de spectateurs déjà atteint");
        }
    }

    private int addTo(LobbyUserData[] arr, LobbyUserData u) {
        for (int i = 0;; i++) {
            if (arr[i] == null) {
                arr[i] = u;
                return i;
            }
        }
    }

    private int addTo(LobbyUserData[] arr, LobbyUserData u, int index) {
        if (arr[index] != null &&
                !u.getUser().getCourriel().equals(arr[index].getUser().getCourriel())
        ) throw new IllegalArgumentException("Un joueur occupe déjà cette position");

        arr[index] = u;
        return index;
    }

    public void devenirRole(LobbyUserData u, LobbyPosition position) {
        LobbyPosition oldPos = removeFromCurrentRole(u);
        LobbyPosition newPos = switchToRole(u, position);
        u.setRole(position.getRole());
        sendData(u.getUser().getAlias() + " a maintenant le role de: " + position.getRole(),
                new DonneesReponse(TypeCommande.ROLE, u, newPos, oldPos));
    }

    public void quitter(LobbyUserData u) {
        LobbyPosition pos = removeFromLobby(u);
        sendData(u.getUser().getAlias() + " a quitté",
                new DonneesReponse(TypeCommande.QUITTER, pos));
    }

    private Predicate<LobbyUserData> isPresent(LobbyUserData utilisateur) {
        return (cpt) -> cpt.equals(utilisateur);
    }

    private Predicate<LobbyUserData> isPresent(SanitaryCompte cpt) {
        return (lud) -> cpt.getCourriel().equals(lud.getUser().getCourriel());
    }


    private synchronized LobbyPosition removeFromList(LobbyUserData utilisateur, List<LobbyUserData> liste) {
        Predicate<LobbyUserData> cond = isPresent(utilisateur);
        for (int i = 0; i < liste.size(); i++) {
            if (cond.test(liste.get(i))) {
                liste.remove(i);
                return new LobbyPosition(utilisateur.getRoleCombat(), i);
            }
        }
        return null;
    }

    private synchronized LobbyPosition removeFromList(LobbyUserData utilisateur, LobbyUserData[] liste) {
        Predicate<LobbyUserData> cond = isPresent(utilisateur);
        for (int i = 0; i < liste.length; i++) {
            if (liste[i] != null && cond.test(liste[i])) {
                liste[i] = null;
                return new LobbyPosition(utilisateur.getRoleCombat(), i);
            }
        }
        return null;
    }

    private synchronized LobbyPosition removeFromLobby(LobbyUserData utilisateur) {
        LobbyPosition pos = removeFromCurrentRole(utilisateur);
        removeFromList(utilisateur, users);
        return pos;
    }

    public LobbyUserData getLobbyUserData(SanitaryCompte compte) throws IllegalArgumentException {
        return users.stream()
                .filter((LobbyUserData lud) -> lud.getUser().getCourriel().equals(compte.getCourriel()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur " + compte.getAlias() + " n'existe pas dans ce lobby"));
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

    public Match getCurrentMatch() {
        return matchInProgress;
    }

    @Override
    public void sendMessage(String transitionMessage) {
        send(transitionMessage);
    }

    @Override
    public void sendRound(Attack blancAttack, Attack rougeAttack) {
        sendData("Attaque du participant en blanc: " + blancAttack, new DonneesReponse(TypeCommande.ATTAQUER, "blanc", blancAttack) );
        sendData("Attaque du participant en rouge: " + rougeAttack, new DonneesReponse(TypeCommande.ATTAQUER, "rouge", rougeAttack));
    }
}
