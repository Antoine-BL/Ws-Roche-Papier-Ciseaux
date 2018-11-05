package cgg.informatique.abl.webSocket.dto.match;

import cgg.informatique.abl.webSocket.dto.lobby.LobbyRole;
import cgg.informatique.abl.webSocket.dto.lobby.LobbyUserData;
import cgg.informatique.abl.webSocket.dto.lobby.RoleColl;
import cgg.informatique.abl.webSocket.entites.Combat;
import cgg.informatique.abl.webSocket.entites.MatchOutcome;
import cgg.informatique.abl.webSocket.messaging.DonneesReponseCommande;
import cgg.informatique.abl.webSocket.messaging.commands.TypeCommande;

public class Match implements SerializableMatch{
    private final MatchUserData arbitre;
    private MatchUserData blanc;
    private MatchUserData rouge;
    private final MatchHandler matchHandler;
    private MatchState state;
    private long lastStateChange;

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
        persistMatch(MatchOutcome.ERREUR_ARBITRE);

        matchHandler.send(String.format("%s a commis une grave faute!", arbitre.getNom()));
        setMatchState(MatchState.EXIT);
        matchEnded();
    }

    public void tie() {
        persistMatch(MatchOutcome.NUL);

        matchHandler.send(String.format("Match nul entre %s et %s", rouge.getNom(), blanc.getNom()));
        setMatchState(MatchState.EXIT);
        matchEnded();
    }

    public void victory(MatchUserData victor, MatchUserData loser) {
        MatchOutcome outcome;
        if (rouge.equals(victor)) {
            outcome = MatchOutcome.VICTOIRE_ROUGE;
        } else if (victor.equals(blanc)){
            outcome = MatchOutcome.VICTORE_BLANC;
        } else {
            throw new IllegalArgumentException("victor invalid");
        }

        persistMatch(outcome);
        matchHandler.send(String.format("%s a remporté son combat contre %s", victor.getNom(), loser.getNom()));
        setMatchState(MatchState.EXIT);
        matchEnded();
    }

    private void persistMatch(MatchOutcome outcome) {
        Combat.Builder()
                .setRouge(this.rouge.getCompte())
                .setBlanc(this.blanc.getCompte())
                .setArbitre(this.arbitre.getCompte())
                .setResultat(outcome)
                .persistCombat();
    }

    private long timeSinceLastChange() {
        return System.currentTimeMillis() - lastStateChange;
    }

    public void matchEnded() {
        this.matchHandler.matchEnded();
    }
}
