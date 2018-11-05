package cgg.informatique.abl.webSocket.entites;

import cgg.informatique.abl.webSocket.dto.lobby.LobbyRole;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;

@Entity
@Table(name = "COMBATS")
public class Combat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, targetEntity = Compte.class)
    @JoinColumn(name="ID_Rouge")
    @JsonBackReference
    private Compte rouge;

    @ManyToOne(fetch = FetchType.EAGER, targetEntity = Compte.class)
    @JoinColumn(name="ID_Blanc")
    @JsonBackReference
    private Compte blanc;

    @ManyToOne(fetch = FetchType.EAGER, targetEntity = Compte.class)
    @JoinColumn(name="ID_Arbitre")
    @JsonBackReference
    private Compte arbitre;

    @Enumerated
    MatchOutcome resultat;

    public Combat() {}

    public Combat(Compte rouge, Compte blanc, Compte arbitre, MatchOutcome resultat) {
        this.rouge = rouge;
        this.blanc = blanc;
        this.arbitre = arbitre;
        this.resultat = resultat;
    }

    public static RougeStep Builder() {
        return new CombatBuilder();
    }

    public interface RougeStep { BlancStep setRouge(Compte rouge); }
    public interface BlancStep { ArbitreStep setBlanc(Compte blanc); }
    public interface ArbitreStep { ResultatStep setArbitre(Compte arbitre); }
    public interface ResultatStep { CombatBuilder setResultat(MatchOutcome resultat); }

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

    public MatchOutcome getResultat() {
        return resultat;
    }

    public void setResultat(MatchOutcome resultat) {
        this.resultat = resultat;
    }
}
