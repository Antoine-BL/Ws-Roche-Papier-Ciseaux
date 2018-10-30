package cgg.informatique.abl.webSocket.dao;

import cgg.informatique.abl.webSocket.entites.Compte;
import cgg.informatique.abl.webSocket.entites.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompteDao extends JpaRepository<Compte, Long> {
    @Override
    Optional<Compte> findById(Long id);
    Optional<Compte> findByCourriel(String courriel);
    boolean existsByCourriel(String courriel);
    boolean existsByAlias(String alias);
}
