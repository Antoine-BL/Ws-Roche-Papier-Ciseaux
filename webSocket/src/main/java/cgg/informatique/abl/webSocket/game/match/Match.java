package cgg.informatique.abl.webSocket.game.match;

import cgg.informatique.abl.webSocket.dto.MatchResult;
import cgg.informatique.abl.webSocket.entites.Groupe;
import cgg.informatique.abl.webSocket.game.lobby.*;
import cgg.informatique.abl.webSocket.entites.Combat;
import cgg.informatique.abl.webSocket.messaging.DonneesReponseCommande;
import cgg.informatique.abl.webSocket.messaging.commands.TypeCommande;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@JsonSerialize(as=SerializableMatch.class)
public class Match implements SerializableMatch{
    private final int POURCENTAGE_FAUTE = 1;

    private final MatchUserData arbitre;
    private MatchUserData blanc;
    private MatchUserData rouge;
    private final MatchHandler matchHandler;
    private MatchState etat;
    private long lastStateChange;
    private MatchResult resultat;
    private boolean fauteArbitre;

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

    private static int[][] tableauResultatsRochePapierCiseaux =
    new int[][] {
        new int[] { 0,  1, 1, 1},
        new int[] {-1,  0, 1,-1},
        new int[] {-1, -1, 0, 1},
        new int[] {-1,  1,-1, 0}
    };

    public Match(LobbyUserData arbitre, MatchHandler matchHandler) {
        this.arbitre = new MatchUserData(arbitre, this);
        this.matchHandler = matchHandler;
        setEtat(MatchState.DEBUT);
    }

    public void choisirParticipantsParmi(RoleColl liste) {
        LobbyUserData rougeChoisi = liste.getRandomUser();
        LobbyUserData blancChoisi = liste.getBestOpponentFor(rougeChoisi);

        rouge = new MatchUserData(rougeChoisi, this);
        blanc = new MatchUserData(blancChoisi, this);
    }

    public void tick() {
        sendData(null, new DonneesReponseCommande(TypeCommande.MATCH_STATE, this));
        if (etat.getDuree() < tempsDepuisDernierEtat()) {
            System.out.println(etat);
            etat.gererFinEtat(this);
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

    public MatchUserData getRouge() {
        return rouge;
    }

    @Override
    public MatchState getMatchState() {
        return this.etat;
    }

    public void setEtat(MatchState etat) {
        sendData(etat.getTransitionMessage(), new DonneesReponseCommande(TypeCommande.MATCH_STATE, ((Lobby)matchHandler).asSerializable()));
        this.etat = etat;
        lastStateChange = System.currentTimeMillis();
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

    public void sendData(String message, DonneesReponseCommande donnees) {
        matchHandler.sendData(message, donnees);
    }

    public static int calculerPointsGagnant(Groupe gagnant, Groupe perdant) {
        int delta = perdant.getId() - gagnant.getId();
        return recompensesSelonDelta.get(delta);
    }

    public void enregistrerCombat() {
        Combat combat = Combat.Builder().setRouge(this.rouge.getCompte())
                .setBlanc(this.blanc.getCompte())
                .setArbitre(this.arbitre.getCompte())
                .setResultat(resultat)
                .build();

        matchHandler.saveMatch(combat);

        sendData(null, new DonneesReponseCommande(TypeCommande.COMBAT, combat));
    }

    private long tempsDepuisDernierEtat() {
        return System.currentTimeMillis() - lastStateChange;
    }

    public boolean arbitreARaison() {
        return arbitreARaison(LobbyRole.ARBITRE);
    }

    public boolean arbitreARaison(LobbyRole role) {
        int bonneReponse = calculerReponse();

        boolean arbitreEstVenerable = arbitre.getLobbyUser().getRole().getId() == 3;
        if (arbitreEstVenerable) return true;

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

    private int calculerReponse() {
        int attBlanc = ThreadLocalRandom.current().nextInt(1, 4);
        int attRouge = ThreadLocalRandom.current().nextInt(1,4);

        blanc.setAttack(Attack.values()[attBlanc]);
        rouge.setAttack(Attack.values()[attRouge]);

        int roleJ1 = rouge.getLobbyUser().getRole().getId();
        int roleJ2 = blanc.getLobbyUser().getRole().getId();
        boolean j1EstVenerable = roleJ1 == 4;
        boolean j2EstVenerable = roleJ2 == 4;
        if (j1EstVenerable || j2EstVenerable) {
            return  roleJ1 - roleJ2;
        }

        return tableauResultatsRochePapierCiseaux[blanc.getAttack().ordinal()][rouge.getAttack().ordinal()];
    }

    public void effectuerMatch() {
       int reponse = calculerReponse();
       fauteArbitre = ThreadLocalRandom.current().nextInt(101) <= POURCENTAGE_FAUTE;
       resultat = new MatchResult();

       if (fauteArbitre) {
           resultat.setCreditsArbitre(-5);
       } else {
           resultat.setCreditsArbitre(1);
       }

       if (reponse == 0) {
           resultat.setPointsRouge(5);
           resultat.setPointsBlanc(5);
       } else if (reponse > 0) {
           resultat.setPointsRouge(10);
           resultat.setPointsBlanc(0);
       } else {
           resultat.setPointsRouge(0);
           resultat.setPointsBlanc(10);
       }

       sendData(null, new DonneesReponseCommande(TypeCommande.ATTAQUER, rouge.getAttack(), blanc.getAttack()));
    }

    public void indiquerGagnant() {
        String message;
        LobbyRole signal;
        if (resultat.getCreditsArbitre() < 0) {
            message = String.format("%s a commis une grave faute", arbitre.getNom());

            if (resultat.getPointsBlanc() == 0 && resultat.getPointsRouge() == 0) {
                signal = ThreadLocalRandom.current().nextInt(0,2) == 0 ? LobbyRole.ROUGE : LobbyRole.BLANC;
            } else {
                signal = resultat.getPointsRouge() == 10 ? LobbyRole.ROUGE : LobbyRole.BLANC;
            }
        } else if (resultat.getPointsRouge() == 10 || resultat.getPointsBlanc() == 10) {
            MatchUserData gagnant = resultat.getPointsRouge() == 10 ? rouge : blanc;
            MatchUserData perdant = resultat.getPointsRouge() == 10 ? blanc : rouge;
            signal = resultat.getPointsRouge() == 10 ? LobbyRole.ROUGE : LobbyRole.BLANC;

            message = String.format("%s a remporté son combat contre %s", gagnant.getNom(), perdant.getNom());
        } else {
            signal = LobbyRole.ARBITRE;
            message = String.format("Match nul entre %s et %s", rouge.getNom(), blanc.getNom());
        }

        sendData(message, new DonneesReponseCommande(TypeCommande.SIGNALER, arbitre, signal));
    }

    public void viderTatami() {
        rouge.getLobbyUser().becomeRole(new LobbyPosition(LobbyRole.COMBATTANT));
        blanc.getLobbyUser().becomeRole(new LobbyPosition(LobbyRole.COMBATTANT));
        arbitre.getLobbyUser().becomeRole(new LobbyPosition(LobbyRole.ARBITRE));
    }

    public void endMatch() {
        matchHandler.matchEnded();
    }
}
