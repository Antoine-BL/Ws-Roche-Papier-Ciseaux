package cgg.informatique.abl.webSocket.game.lobby;

import cgg.informatique.abl.webSocket.game.match.Match;
import cgg.informatique.abl.webSocket.game.match.MatchHandler;
import cgg.informatique.abl.webSocket.game.match.SerializableMatch;
import cgg.informatique.abl.webSocket.entites.*;
import cgg.informatique.abl.webSocket.messaging.DonneesReponseCommande;
import cgg.informatique.abl.webSocket.messaging.Reponse;
import cgg.informatique.abl.webSocket.messaging.ReponseCommande;
import cgg.informatique.abl.webSocket.messaging.commands.TypeCommande;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.*;

public class Lobby implements Runnable, MatchHandler, SerializableLobby {
    private static final String OUTPUT_TOPIC = "/topic/battle/lobby";
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

    private HashSet<LobbyUserData> users;
    private RoleColl spectateurs;
    private RoleColl combattants;
    private HashSet<LobbyUserData> ailleurs;
    private LobbyUserData rouge;
    private LobbyUserData blanc;
    private LobbyUserData arbitre;
    private int nbMatchArbitre = 0;

    private Match matchInProgress;
    private final SimpMessagingTemplate messaging;
    private LobbyContext lobbyContext;

    private boolean running = true;

    public Lobby(SimpMessagingTemplate messaging, LobbyContext ctxt) {
        this.messaging = messaging;
        this.lobbyContext = ctxt;

        users = new HashSet<>(27);
        spectateurs = new RoleColl(LobbyRole.SPECTATEUR);
        combattants = new RoleColl(LobbyRole.COMBATTANT);
        ailleurs = new HashSet<>();
    }

    @Override
    public void run() {
        send("Lobby ouvert!");
        mainLoop();
        send("Lobby closed");
        lobbyContext.lobbyClosed();
    }

    public void stop() {
        this.running = false;
    }

    public void updateUserInfo(Compte user) {
        try {
            LobbyUserData lud = getLobbyUserData(user);
            lud.setUser(getUser(user.getUsername()));
        } catch (IllegalArgumentException ignored) {}
    }

    private void mainLoop(){
        long tickStart;
        long processingTime = 0;
        while (running) {
            try { Thread.sleep(Math.max(TICK_DURATION - processingTime, 0));} catch (InterruptedException ignored) {}
            tickStart = System.currentTimeMillis();

            if (isReadyForMatch()) startMatch();
            if (matchInProgress != null) matchInProgress.tick();
            processingTime = System.currentTimeMillis() - tickStart;
        }
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

    public void connect(Compte u) {
        LobbyUserData userData = new LobbyUserData(u, this);

        this.users.add(userData);
        userData.becomeRole(new LobbyPosition(LobbyRole.AILLEURS));

        sendData("Bienvenue au lobby " + u.getAlias(), new DonneesReponseCommande(TypeCommande.JOINDRE, this.asSerializable()));
    }

    public void posChanged(LobbyUserData user, LobbyPosition newPos, LobbyPosition oldPos) {
        sendData(String.format("%s a maintenant le role de %s", user.getAlias(), user.getRoleCombat()),
                new DonneesReponseCommande(TypeCommande.ROLE, this.asSerializable()));
    }

    public void quitter(LobbyUserData u) {
        LobbyPosition pos = removeFromLobby(u);
        sendData(u.getUser().getAlias() + " a quitté",
                new DonneesReponseCommande(TypeCommande.JOINDRE, this.asSerializable()));
    }

    @Override
    public void ensureCoherentRole() {
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
        Match match = new Match(this.arbitre, this);
        match.choisirParticipantsParmi(combattants);

        match.getRouge().getLobbyUser().becomeRole(new LobbyPosition(LobbyRole.ROUGE));
        match.getBlanc().getLobbyUser().becomeRole(new LobbyPosition(LobbyRole.BLANC));

        nbMatchArbitre++;
        matchInProgress = match;
    }

    private boolean isReadyForMatch() {
        return matchInProgress == null && combattants.size() >= 2 && arbitre != null;
    }

    @Override
    public void matchEnded() {
        send("fin du match");
        matchInProgress = null;
        if (nbMatchArbitre >= 5) {
            send("Il faut un nouvel arbitre");
            arbitre.becomeRole(new LobbyPosition(LobbyRole.SPECTATEUR));
            resetNbMatchArbitre();
        }
    }

    @Override
    public void saveMatch(Combat combat) {
        this.lobbyContext.persistMatch(combat);

        updateUserInfo(matchInProgress.getRouge().getCompte());
        updateUserInfo(matchInProgress.getArbitre().getCompte());
        updateUserInfo(matchInProgress.getBlanc().getCompte());
    }

    public Match getCurrentMatch() {
        return matchInProgress;
    }

    @Override
    public LobbyUserData[] getAilleurs() {
        LobbyUserData[] ailleursArr = new LobbyUserData[ailleurs.size()];

        ailleurs.toArray(ailleursArr);

        return ailleursArr;
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

    public int getNbMatchArbitre() {
        return nbMatchArbitre;
    }

    public void resetNbMatchArbitre() {
        this.nbMatchArbitre = 0;
    }

    public void addAilleurs(LobbyUserData user) {
        this.ailleurs.add(user);
        sendData("Connexion de " + user.getCourriel(), new DonneesReponseCommande(TypeCommande.CONNECTER, user));
    }

    public void removeAilleurs(LobbyUserData user) {
        this.ailleurs.remove(user);
        sendData("Deconnexion de " + user.getCourriel(), new DonneesReponseCommande(TypeCommande.DECONNECTER, user));
    }

    public Compte getUser(String user) {
        return lobbyContext.getUser(user);
    }
}
