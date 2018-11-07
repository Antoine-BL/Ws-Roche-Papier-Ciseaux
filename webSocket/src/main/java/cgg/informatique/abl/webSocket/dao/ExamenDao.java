package cgg.informatique.abl.webSocket.dao;

import cgg.informatique.abl.webSocket.entites.Compte;
import cgg.informatique.abl.webSocket.entites.Examen;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExamenDao extends JpaRepository<Examen, Long> {
    List<Examen> findByEleve(Compte eleve);
}
