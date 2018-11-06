package cgg.informatique.abl.webSocket.dto;

import cgg.informatique.abl.webSocket.entites.Compte;
import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;

public class ExamDto {
    private Long id;
    private Long professeur;
    private Long eleve;
    private boolean reussi;
    private long temps;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProfesseur() {
        return professeur;
    }

    public void setProfesseur(Long professeur) {
        this.professeur = professeur;
    }

    public Long getEleve() {
        return eleve;
    }

    public void setEleve(Long eleve) {
        this.eleve = eleve;
    }

    public boolean isReussi() {
        return reussi;
    }

    public void setReussi(boolean reussi) {
        this.reussi = reussi;
    }

    public long getTemps() {
        return temps;
    }

    public void setTemps(long temps) {
        this.temps = temps;
    }
}
