package cgg.informatique.abl.webSocket.dto.match;

import cgg.informatique.abl.webSocket.dto.lobby.LobbyRole;
import cgg.informatique.abl.webSocket.dto.lobby.LobbyUserData;
import cgg.informatique.abl.webSocket.dto.lobby.RoleColl;
import cgg.informatique.abl.webSocket.entites.Combat;
import cgg.informatique.abl.webSocket.messaging.DonneesReponseCommande;
import cgg.informatique.abl.webSocket.messaging.commands.TypeCommande;

import java.util.HashMap;
import java.util.Map;

public class Match implements SerializableMatch{
    private final MatchUserData arbitre;
    private MatchUserData blanc;
    private MatchUserData rouge;
    private final MatchHandler matchHandler;
    private MatchState state;
    private long lastStateChange;

    static Map<Integer, Integer> recompensesSelonDelta;
    static {
        recompensesSelonDelta = new HashMap<>();

        recompensesSelonDelta.put(6, 50);
        recompensesSelonDelta.put(5, 30);
        recompensesSelonDelta.put(4, 25);
        recompensesSelonDelta.put(3, 20);
        recompensesSelonDelta.put(2, 15);
        recompensesSelonDelta.put(1, 12);
        recompensesSelonDelta.put(0, 10);
        recompensesSelonDelta.put(-1, 9);
        recompensesSelonDelta.put(-2, 7);
        recompensesSelonDelta.put(-3, 5);
        recompensesSelonDelta.put(-4, 3);
        recompensesSelonDelta.put(-5, 2);
        recompensesSelonDelta.put(-6, 1);
    }

    public Match(LobbyUserData arbitre, MatchHandler matchHandler) {
        this.arbitre = new MatchUserData(arbitre, this);
        this.matchHandler = matchHandler;
        setMatchState(MatchState.WAITING);
    }

    public void choisirParticipantsParmi(RoleColl liste) {
        LobbyUserData rougeChoisi = liste.getRandomUser();
        LobbyUserData blancChoisi = liste.getBestOpponentFor(rougeChoisi);

        rouge = new MatchUserData(rougeChoisi, this);
        blanc = new MatchUserData(blancChoisi, this);
    }

    public void tick() {
        if (state.getDuration() < timeSinceLastChange()) {
            state.handleTimeout(this);
        }
    }

    public void determineFault() {
        if (rouge.isReadyForFight() &&
            blanc.isReadyForFight()) {
            refAtFault();
        } else if (!rouge.isReadyForFight()) {
            victory(blanc, rouge);
        } else if (!rouge.isReadyForFight()) {
            victory(rouge, blanc);
        }
    }

    public void handleRound() {
        matchHandler.send(String.format("rouge: %s, blanc: %s", rouge.getAttack(), blanc.getAttack()));
        setMatchState(MatchState.DECIDE);
    }

    public void disqualify() {
        if (blanc.getState() == PlayerState.ESTRADE && rouge.getState() == PlayerState.ESTRADE) setMatchState(MatchState.EXIT);
        if (blanc.getState() == PlayerState.ESTRADE)  victory(rouge, blanc);
        if (rouge.getState() == PlayerState.ESTRADE)  victory(blanc, rouge);
    }

    public void rougeLeft() {
        playerLeft(rouge, blanc);
    }

    public void blancLeft() {
        playerLeft(blanc, rouge);
    }

    public void playerLeft(MatchUserData leaver, MatchUserData other) {
        victory(other, leaver);
    }

    public MatchUserData getParticipant(LobbyUserData user) {
        if (user.getRoleCombat() == LobbyRole.ARBITRE) {
            return arbitre;
        } else if (user.getRoleCombat() == LobbyRole.BLANC) {
            return blanc;
        } else if (user.getRoleCombat() == LobbyRole.ROUGE) {
            return rouge;
        } else {
            throw new IllegalArgumentException("");
        }
    }

    public MatchUserData getRouge() {
        return rouge;
    }

    @Override
    public MatchState getMatchState() {
        return this.state;
    }

    @Override
    public Long getChrono() {
        return this.state.getDuration() - lastStateChange;
    }

    @Override
    public MatchUserData getArbitre() {
        return this.arbitre;
    }

    public MatchUserData getBlanc() {
        return blanc;
    }

    public void setMatchState(MatchState state) {
        this.state = state;
        this.lastStateChange = System.currentTimeMillis();
        matchHandler.sendData(state.getTransitionMessage(), new DonneesReponseCommande(TypeCommande.MATCH_STATE, this.state));
    }

    public void sendData(String message, DonneesReponseCommande donnees) {
        matchHandler.sendData(message, donnees);
    }

    public void refAtFault() {
        int ptsBlanc = calculerPointsGagant(blanc, rouge) / 2;
        int ptsRouge = calculerPointsGagant(rouge, blanc) / 2;
        int ptsArbitre = -5;

        persistMatch(ptsRouge, ptsBlanc, ptsArbitre);

        matchHandler.send(String.format("%s a commis une grave faute!", arbitre.getNom()));
        setMatchState(MatchState.EXIT);
        matchEnded();
    }

    public void tie() {
        int ptsBlanc = calculerPointsGagant(blanc, rouge) / 2;
        int ptsRouge = calculerPointsGagant(rouge, blanc) / 2;
        int ptsArbitre = 1;

        persistMatch(ptsRouge, ptsBlanc, ptsArbitre);

        matchHandler.send(String.format("Match nul entre %s et %s", rouge.getNom(), blanc.getNom()));
        setMatchState(MatchState.EXIT);
        matchEnded();
    }

    public void victory(MatchUserData victor, MatchUserData loser) {
        int ptsRouge;
        int ptsBlanc;
        int ptsArbitre = 1;

        if (rouge.equals(victor)) {
            ptsRouge = calculerPointsGagant(rouge, blanc);
            ptsBlanc = 0;
        } else if (victor.equals(blanc)){
            ptsBlanc = calculerPointsGagant(blanc, rouge);
            ptsRouge = 0;
        } else {
            throw new IllegalArgumentException("victor invalid");
        }

        persistMatch(ptsRouge, ptsBlanc, ptsArbitre);

        matchHandler.send(String.format("%s a remporté son combat contre %s", victor.getNom(), loser.getNom()));
        setMatchState(MatchState.EXIT);
        matchEnded();
    }

    private int calculerPointsGagant(MatchUserData gagnant, MatchUserData perdant) {
        int delta = perdant.getLobbyUser().getGroupeObj().getId() - gagnant.getLobbyUser().getGroupeObj().getId();
        return recompensesSelonDelta.get(delta);
    }

    private void persistMatch(int pointsRouge, int pointsBlanc, int creditsArbitre) {
        Combat.Builder()
                .setRouge(this.rouge.getCompte())
                .setBlanc(this.blanc.getCompte())
                .setArbitre(this.arbitre.getCompte())
                .setResultat(pointsRouge, pointsBlanc, creditsArbitre)
                .persistCombat();
    }

    private long timeSinceLastChange() {
        return System.currentTimeMillis() - lastStateChange;
    }

    public void matchEnded() {
        if (rouge != null) matchHandler.quitter(rouge.getLobbyUser());
        if (blanc != null) matchHandler.quitter(blanc.getLobbyUser());

        this.matchHandler.matchEnded();
    }
}
