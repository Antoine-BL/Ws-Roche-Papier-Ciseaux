package cgg.informatique.abl.webSocket.dao;

import cgg.informatique.abl.webSocket.entites.Avatar;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AvatarDao extends JpaRepository<Avatar, Long> {
}
