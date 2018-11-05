package cgg.informatique.abl.webSocket.entites;

import cgg.informatique.abl.webSocket.dto.lobby.LobbyUserData;
import cgg.informatique.abl.webSocket.dto.match.Match;
import com.sun.corba.se.pept.transport.OutboundConnectionCache;

import java.util.HashMap;
import java.util.Map;

public enum MatchOutcome {
    VICTOIRE_ROUGE((Compte compte, Combat combat) -> {
        return calculerRecompense(compte, combat, combat.getRouge(), combat.getBlanc());
    }),
    VICTORE_BLANC((Compte compte, Combat combat) -> {
        return calculerRecompense(compte, combat, combat.getBlanc(), combat.getRouge());
    }),
    NUL((Compte compte, Combat combat) -> {
        int idBlanc = combat.getBlanc().getGroupeObj().getId();
        int idRouge = combat.getRouge().getGroupeObj().getId();
        int recompense;

        if (compte.equals(combat.getBlanc())) {
            int delta = idRouge - idBlanc;
            recompense = CombatBuilder.recompensesSelonDelta.get(delta) / 2;
        } else if(compte.equals(combat.getRouge())) {
            int delta = idBlanc - idRouge;
            recompense = CombatBuilder.recompensesSelonDelta.get(delta) / 2;
        } else {
            recompense = 1;
        }

        return recompense;
    }),
    ERREUR_ARBITRE(
        (Compte compte, Combat combat) -> {
            int idBlanc = combat.getBlanc().getGroupeObj().getId();
            int idRouge = combat.getRouge().getGroupeObj().getId();
            int recompense;

            if (compte.equals(combat.getBlanc())) {
                int delta = idRouge - idBlanc;
                recompense = CombatBuilder.recompensesSelonDelta.get(delta) / 2;
            } else if(compte.equals(combat.getRouge())) {
                int delta = idBlanc - idRouge;
                recompense = CombatBuilder.recompensesSelonDelta.get(delta) / 2;
            } else {
                recompense = -5;
            }

            return recompense;
        }
    );

    private static int calculerRecompense(Compte compte, Combat combat, Compte gagnant, Compte perdant) {
        int idBlanc = combat.getBlanc().getGroupeObj().getId();
        int idRouge = combat.getRouge().getGroupeObj().getId();
        int recompense;

        if (compte.equals(gagnant)) {
            int delta = idRouge - idBlanc;
            recompense = CombatBuilder.recompensesSelonDelta.get(delta);
        } else if(compte.equals(perdant)) {
            recompense = 0;
        } else {
            recompense = 1;
        }

        return recompense;
    }

    private OutcomeHandler handler;
    MatchOutcome(OutcomeHandler handler) {
        this.handler = handler;
    }

    interface OutcomeHandler { int calculerPoints(Compte compte, Combat combat); }

    public int calculerRecompensePour(Compte compte, Combat combat) {
        return this.handler.calculerPoints(compte, combat);
    }
}
