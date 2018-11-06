package cgg.informatique.abl.webSocket.dao;

import cgg.informatique.abl.webSocket.entites.Groupe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupeDao extends JpaRepository<Groupe,Integer> { }
