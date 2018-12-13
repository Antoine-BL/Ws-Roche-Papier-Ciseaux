package cgg.informatique.abl.webSocket.game.match;

import cgg.informatique.abl.webSocket.entites.Groupe;
import cgg.informatique.abl.webSocket.game.lobby.LobbyRole;
import cgg.informatique.abl.webSocket.game.lobby.LobbyUserData;
import cgg.informatique.abl.webSocket.game.lobby.RoleColl;
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
    private MatchState etat;
    private long lastStateChange;
    private Combat resultat;

    private static Map<Integer, Integer> recompensesSelonDelta;
    static {
        recompensesSelonDelta = new HashMap<>();

        recompensesSelonDelta.put(7, 1);
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
        recompensesSelonDelta.put(-7, 1);
    }

    private boolean arbitreReste;

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
        sendData(null, new DonneesReponseCommande(TypeCommande.MATCH_STATE, getChrono(), this.etat, this.etat.getMessage()));
        if (etat.getDuree() < tempsDepuisDernierEtat()) {
            etat.gererFinEtat(this);
        }

        if (etat == MatchState.EXIT) {
            if (rouge.getLobbyUser().getRoleCombat() != LobbyRole.ROUGE
                && blanc.getLobbyUser().getRoleCombat() != LobbyRole.BLANC
                && arbitreReste) {
                setMatchState(MatchState.OVER);
            }
        }
    }

    public void determineFault() {
        if (rouge.isReadyForFight() &&
            blanc.isReadyForFight()) {
            refAtFault();
        } else if (!rouge.isReadyForFight()) {
            rougeLeft();
        } else if (!blanc.isReadyForFight()) {
            blancLeft();
        }
    }

    public void handleRound() {
        matchHandler.sendData(String.format("Attaque Rouge: %s",rouge.getAttack()),
                new DonneesReponseCommande(TypeCommande.ATTAQUER, rouge, rouge.getAttack()));
        matchHandler.sendData(String.format("Attaque Blanc: %s",blanc.getAttack()),
                new DonneesReponseCommande(TypeCommande.ATTAQUER, blanc, blanc.getAttack()));
        setMatchState(MatchState.DECIDE);
    }

    public void disqualify() {
        if (blanc.getRoleCombat() != LobbyRole.BLANC && rouge.getRoleCombat() != LobbyRole.ROUGE){
            setMatchState(MatchState.EXIT);
        } else if (blanc.getRoleCombat() != LobbyRole.BLANC)  {
            blancLeft();
        } else if (rouge.getRoleCombat() != LobbyRole.ROUGE) {
            rougeLeft();
        } else {
            refAtFault();
        }
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
        return this.etat;
    }

    @Override
    public Long getChrono() {
        try {
            return (this.etat.getDuree() - (System.currentTimeMillis() - lastStateChange)) / 1000;
        } catch (ArithmeticException e) {
            return 0L;
        }
    }

    @Override
    public MatchUserData getArbitre() {
        return this.arbitre;
    }

    public MatchUserData getBlanc() {
        return blanc;
    }

    public void setMatchState(MatchState etat) {
        this.etat = etat;
        switch (etat) {
            case EXIT:
                //Action 1
                break;
            case OVER:
                //Action 2
                break;
            case DECIDE:
                //Action 3
                break;
            case READY:
                //Action 4
                break;
            case START:
                //Action 5
                break;
            case WAITING:
                //Action 6
                break;
        }
        this.lastStateChange = System.currentTimeMillis();
        matchHandler.sendData(etat.getTransitionMessage(), new DonneesReponseCommande(TypeCommande.MATCH_STATE, getChrono(),this.etat, this.etat.getMessage()));
    }

    public void sendData(String message, DonneesReponseCommande donnees) {
        matchHandler.sendData(message, donnees);
    }

    public void refAtFault() {
        int ptsBlanc = 10;
        int ptsRouge = 10;
        int ptsArbitre = -5;

        matchHandler.send(String.format("%s a commis une grave faute!", arbitre.getNom()));
        setMatchState(MatchState.EXIT);

        enregistrerCombat(ptsRouge, ptsBlanc, ptsArbitre);
    }

    public void tie() {
        int ptsBlanc = 10;
        int ptsRouge = 10;
        int ptsArbitre = 1;

        sendData(null, new DonneesReponseCommande(TypeCommande.SIGNALER, "IPPON", "NUL"));
        matchHandler.send(String.format("Match nul entre %s et %s", rouge.getNom(), blanc.getNom()));
        setMatchState(MatchState.EXIT);

        enregistrerCombat(ptsRouge, ptsBlanc, ptsArbitre);
    }

    public void victory(MatchUserData victor, MatchUserData loser) {
        sendData(null, new DonneesReponseCommande(TypeCommande.SIGNALER, "IPPON", victor));
        matchHandler.send(String.format("%s a remporté son combat contre %s", victor.getNom(), loser.getNom()));
        setMatchState(MatchState.EXIT);

        if (victor.getRoleCombat() == LobbyRole.ROUGE) {
            enregistrerCombat(10, 0, 1);
        } else {
            enregistrerCombat(0, 10, 1);
        }
    }

    public static int calculerPointsGagnant(Groupe gagnant, Groupe perdant) {
        int delta = perdant.getId() - gagnant.getId();
        return recompensesSelonDelta.get(delta);
    }

    private void enregistrerCombat(int pointsRouge, int pointsBlanc, int creditsArbitre) {
        Combat combat = Combat.Builder().setRouge(this.rouge.getCompte())
                .setBlanc(this.blanc.getCompte())
                .setArbitre(this.arbitre.getCompte())
                .setCeintures(this.blanc.getUser().getGroupe(), this.rouge.getUser().getGroupe())
                .setResultat(pointsRouge, pointsBlanc, creditsArbitre)
                .build();
        resultat = combat;
    }

    private long tempsDepuisDernierEtat() {
        return System.currentTimeMillis() - lastStateChange;
    }

    public void matchEnded() {
        if (rouge.getLobbyUser().getRoleCombat() == LobbyRole.ROUGE) matchHandler.quitter(rouge.getLobbyUser());
        if (blanc.getLobbyUser().getRoleCombat() == LobbyRole.BLANC) matchHandler.quitter(blanc.getLobbyUser());
        if (!arbitreReste && arbitre.getLobbyUser().getRoleCombat() == LobbyRole.ARBITRE) matchHandler.quitter(arbitre.getLobbyUser());
        this.matchHandler.saveMatch(resultat);
        matchHandler.matchEnded();
    }

    public boolean arbitreARaison() {
        return arbitreARaison(LobbyRole.ARBITRE);
    }

    public boolean arbitreARaison(LobbyRole role) {
        int bonneReponse = trouverBonneReponse(rouge, blanc);

        if (arbitre.getLobbyUser().getRole().getId() == 3) return true;

        if (role == LobbyRole.ARBITRE){
            return bonneReponse == 0;
        } else if (role == LobbyRole.ROUGE) {
            return bonneReponse > 0;
        } else if (role == LobbyRole.BLANC) {
            return  bonneReponse < 0;
        } else {
            throw new IllegalArgumentException();
        }
    }

    private int trouverBonneReponse(MatchUserData joueur1, MatchUserData joueur2) {
        int roleJ1 = joueur1.getLobbyUser().getRole().getId();
        int roleJ2 = joueur2.getLobbyUser().getRole().getId();
        int[][] resultats = new int[][] {
                new int[] { 0,  1, 1, 1},
                new int[] {-1,  0, 1,-1},
                new int[] {-1, -1, 0, 1},
                new int[] {-1,  1,-1, 0}
        };

        if (roleJ1 == 3 || roleJ2 == 3) {
            return  roleJ1 - roleJ2;
        }

        return resultats[joueur2.getAttack().ordinal()][joueur1.getAttack().ordinal()];
    }

    public void setArbitreReste(boolean reste) {
        if (reste) {
            matchHandler.send("L'arbitre va rester");
        }
        this.arbitreReste = reste;
    }
}
