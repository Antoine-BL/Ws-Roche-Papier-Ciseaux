package cgg.informatique.abl.webSocket.dao;

import cgg.informatique.abl.webSocket.entites.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleDao extends JpaRepository<Role, Integer> { }
