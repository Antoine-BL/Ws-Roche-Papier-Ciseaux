package cgg.informatique.abl.webSocket.dao;

import cgg.informatique.abl.webSocket.dto.CompteDto;
import cgg.informatique.abl.webSocket.entites.Compte;

public interface CompteService {
    Compte registerNewUserAccount(CompteDto compteDto) throws IllegalArgumentException;
}
