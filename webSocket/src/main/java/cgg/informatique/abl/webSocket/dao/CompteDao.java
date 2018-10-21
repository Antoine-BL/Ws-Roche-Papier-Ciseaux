package cgg.informatique.abl.webSocket.dao;

import cgg.informatique.abl.webSocket.entites.Compte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompteDao extends JpaRepository<Compte, Long> {
    @Override
    Optional<Compte> findById(Long id);
    boolean existsByCourriel(String courriel);
}
