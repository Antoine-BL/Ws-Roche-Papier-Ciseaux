package cgg.informatique.abl.webSocket.dao;

import cgg.informatique.abl.webSocket.entites.Combat;
import cgg.informatique.abl.webSocket.entites.Compte;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CombatDao extends JpaRepository<Combat, Long> {
    List<Combat> findAllByRouge(Compte rouge);
    List<Combat> findAllByBlanc(Compte blanc);
}
