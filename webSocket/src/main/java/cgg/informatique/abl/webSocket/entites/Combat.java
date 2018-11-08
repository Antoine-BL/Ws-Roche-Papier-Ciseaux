package cgg.informatique.abl.webSocket.entites;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;

@Entity
@Table(name = "COMBATS")
public class Combat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, targetEntity = Compte.class)
    @JoinColumn(name="ID_Rouge")
    @JsonBackReference(value="combat-rouge")
    private Compte rouge;

    @ManyToOne(fetch = FetchType.EAGER, targetEntity = Compte.class)
    @JoinColumn(name="ID_Blanc")
    @JsonBackReference(value="combat-blanc")
    private Compte blanc;

    @ManyToOne(fetch = FetchType.EAGER, targetEntity = Compte.class)
    @JoinColumn(name="ID_Arbitre")
    @JsonBackReference(value="combat-arbitre")
    private Compte arbitre;

    @Column(name = "POINTSROUGE")
    private int pointsRouge;
    @Column(name = "POINTSBLANC")
    private int pointsBlanc;
    @Column(name = "CREDITSARBITRE")
    private int creditsArbitre;
    @Column(name = "TEMPS")
    private long temps;

    public Combat() {}

    public Combat(Compte rouge, Compte blanc, Compte arbitre, int pointsRouges, int pointsBlancs, int creditsArbitres) {
        this.rouge = rouge;
        this.blanc = blanc;
        this.arbitre = arbitre;
        this.pointsRouge = pointsRouges;
        this.pointsBlanc = pointsBlancs;
        this.creditsArbitre = creditsArbitres;
    }

    public static RougeStep Builder() {
        return new CombatBuilder();
    }

    public interface RougeStep { BlancStep setRouge(Compte rouge); }
    public interface BlancStep { ArbitreStep setBlanc(Compte blanc); }
    public interface ArbitreStep { ResultatStep setArbitre(Compte arbitre); }
    public interface ResultatStep { CombatBuilder setResultat(int pointsRouge, int pointsBlanc, int creditArbitre); }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Compte getRouge() {
        return rouge;
    }

    public void setRouge(Compte rouge) {
        this.rouge = rouge;
    }

    public Compte getBlanc() {
        return blanc;
    }

    public void setBlanc(Compte blanc) {
        this.blanc = blanc;
    }

    public Compte getArbitre() {
        return arbitre;
    }

    public void setArbitre(Compte arbitre) {
        this.arbitre = arbitre;
    }


    public int getPointsRouge() {
        return pointsRouge;
    }

    public void setPointsRouge(int pointsRouge) {
        this.pointsRouge = pointsRouge;
    }

    public int getPointsBlanc() {
        return pointsBlanc;
    }

    public void setPointsBlanc(int pointsBlanc) {
        this.pointsBlanc = pointsBlanc;
    }

    public int getCreditsArbitre() {
        return creditsArbitre;
    }

    public void setCreditsArbitre(int creditsArbitre) {
        this.creditsArbitre = creditsArbitre;
    }

    public long getTemps() {
        return temps;
    }

    public void setTemps(long temps) {
        this.temps = temps;
    }
}
