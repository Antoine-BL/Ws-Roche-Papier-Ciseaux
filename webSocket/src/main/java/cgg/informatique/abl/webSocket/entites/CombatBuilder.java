package cgg.informatique.abl.webSocket.entites;

import cgg.informatique.abl.webSocket.dao.CombatDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CombatBuilder implements Combat.RougeStep, Combat.BlancStep, Combat.ArbitreStep, Combat.ResultatStep {
    private Compte rouge;
    private Compte blanc;
    private Compte arbitre;
    private int pointsRouge;
    private int pointsBlanc;
    private int creditsArbitre;

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

    @Override
    public CombatBuilder setResultat(int pointsRouge, int pointsBlanc, int creditArbitre) {
        this.pointsRouge = pointsRouge;
        this.pointsBlanc = pointsBlanc;
        this.creditsArbitre = creditArbitre;

        return this;
    }

    public void persistCombat() {
        Combat combat = new Combat(rouge, blanc, arbitre, pointsRouge, pointsBlanc, creditsArbitre);
        combatDao.save(combat);
    }
}