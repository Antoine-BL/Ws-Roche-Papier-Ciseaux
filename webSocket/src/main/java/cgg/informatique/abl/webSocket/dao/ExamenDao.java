package cgg.informatique.abl.webSocket.dao;

import cgg.informatique.abl.webSocket.entites.Examen;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExamenDao extends JpaRepository<Examen, Long> { }
