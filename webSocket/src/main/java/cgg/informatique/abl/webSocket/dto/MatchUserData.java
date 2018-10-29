package cgg.informatique.abl.webSocket.dto;

public class MatchUserData {
    private LobbyUserData user;
    private PlayerState state = PlayerState.ESTRADE;
    private boolean saluting;
    private Attack attack = Attack.RIEN;

    public MatchUserData(LobbyUserData user) {
        this.user = user;
    }

    public LobbyRole getRole(){
        return user.getRole();
    }

    public void leavePenalty() {

    }

    public void loseAgainst(MatchUserData opponent) {

    }

    public void winAgainst(MatchUserData opponent) {

    }

    public void tieAgainst(MatchUserData opponent) {

    }

    public void setState(PlayerState state) {
        this.state = state;
    }

    public PlayerState getState() {
        return state;
    }

    public boolean isReadyForFight() {
        return state == PlayerState.TATAMI && saluting;
    }

    public void setSaluting(boolean saluting) {
        this.saluting = saluting;
    }

    public boolean isSaluting() {
        return saluting;
    }

    public Attack getAttack() {
        return attack;
    }

    public void setAttack(Attack attack) {
        this.attack = attack;
    }

    public String getNom() {
        return user.getUser().getAlias();
    }
}
