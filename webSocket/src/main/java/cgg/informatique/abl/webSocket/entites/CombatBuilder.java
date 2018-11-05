package cgg.informatique.abl.webSocket.entites;

import cgg.informatique.abl.webSocket.dao.CombatDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CombatBuilder implements Combat.RougeStep, Combat.BlancStep, Combat.ArbitreStep, Combat.ResultatStep {
    private Compte rouge;
    private Compte blanc;
    private Compte arbitre;
    private MatchOutcome resultat;


    static Map<Integer, Integer> recompensesSelonDelta = new HashMap<>();
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

    @Autowired private CombatDao combatDao;

    CombatBuilder(){}

    public Combat.BlancStep setRouge(Compte rouge) {
        this.rouge = rouge;
        return this;
    }

    public Combat.ArbitreStep setBlanc(Compte blanc) {
        this.blanc = blanc;
        return this;
    }

    public Combat.ResultatStep setArbitre(Compte arbitre) {
        this.arbitre = arbitre;
        return this;
    }

    public CombatBuilder setResultat(MatchOutcome resultat) {
        this.resultat = resultat;
        return this;
    }

    public Combat persistCombat() {
        Combat combat = new Combat(rouge, blanc, arbitre, resultat);
        return combatDao.save(combat);
    }
}