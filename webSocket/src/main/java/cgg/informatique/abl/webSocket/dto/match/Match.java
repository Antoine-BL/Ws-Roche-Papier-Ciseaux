package cgg.informatique.abl.webSocket.dto.match;

import cgg.informatique.abl.webSocket.dto.lobby.LobbyRole;
import cgg.informatique.abl.webSocket.dto.lobby.LobbyUserData;
import cgg.informatique.abl.webSocket.messaging.DonneesReponseCommande;
import cgg.informatique.abl.webSocket.messaging.commands.TypeCommande;

public class Match implements SerializableMatch{
    private final MatchUserData arbitre;
    private final MatchUserData blanc;
    private final MatchUserData rouge;
    private final MatchHandler matchHandler;
    private MatchState state;
    private long lastStateChange;

    public Match(LobbyUserData arbitre, LobbyUserData blanc, LobbyUserData rouge, MatchHandler matchHandler) {
        this.arbitre = new MatchUserData(arbitre, this);
        this.blanc = new MatchUserData(blanc, this);
        this.rouge = new MatchUserData(rouge, this);
        this.matchHandler = matchHandler;
        setMatchState(MatchState.WAITING);
    }

    public void tick() {
        if (state.getDuration() < timeSinceLastChange()) {
            handleStateTimeout();
        }
    }

    private void handleStateTimeout() {
        switch (state) {
            case WAITING:
                disqualify();
                break;
            case READY:
                determineFault();
                break;
            case START:
                handleRound();
                break;
            case DECIDE:
                refLeft();
                break;
            case OVER:
                matchHandler.matchEnded();
                break;
            case EXIT:
                setMatchState(MatchState.OVER);
                break;
        }
    }

    private void determineFault() {
        if (rouge.isReadyForFight() &&
            blanc.isReadyForFight()) {
            refLeft();
        } else if (!rouge.isReadyForFight()) {
            matchEnd(blanc, rouge);
        } else if (!rouge.isReadyForFight()) {
            matchEnd(rouge, blanc);
        }
    }

    private void handleRound() {
        matchHandler.send(String.format("rouge: %s, blanc: %s", rouge.getAttack(), blanc.getAttack()));
        setMatchState(MatchState.DECIDE);
    }

    private void disqualify() {
        if (blanc.getState() == PlayerState.ESTRADE && rouge.getState() == PlayerState.ESTRADE) setMatchState(MatchState.EXIT);

        if (blanc.getState() == PlayerState.ESTRADE)  matchEnd(rouge, blanc);
        if (rouge.getState() == PlayerState.ESTRADE)  matchEnd(blanc, rouge);
    }

    public void playerLeft(MatchUserData player) {
        if (player.getRoleCombat() == LobbyRole.ARBITRE) {
            refLeft();
        } else if (player.getRoleCombat() == LobbyRole.BLANC) {
            matchEnd(rouge, blanc);
        } else if (player.getRoleCombat() == LobbyRole.ROUGE) {
            matchEnd(blanc, rouge);
        }
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

    public void deciderVerdict(MatchUserData vainqueur, MatchUserData perdant){
        matchEnd(vainqueur, perdant);
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

    private void refLeft() {
        tie();
        arbitre.leavePenalty();
    }

    public void tie() {
        blanc.tieAgainst(rouge);
        rouge.tieAgainst(blanc);
        matchHandler.send(String.format("Match nul entre %s et %s", rouge.getNom(), blanc.getNom()));
    }

    private void matchEnd(MatchUserData victor, MatchUserData loser) {
        victor.winAgainst(loser);
        loser.loseAgainst(victor);
        matchHandler.send(String.format("%s a remporté son combat contre %s", victor.getNom(), loser.getNom()));
    }

    private long timeSinceLastChange() {
        return System.currentTimeMillis() - lastStateChange;
    }
}
