package cgg.informatique.abl.webSocket.dao;

import cgg.informatique.abl.webSocket.entites.Combat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CombatDao extends JpaRepository<Combat, Long> {
}
