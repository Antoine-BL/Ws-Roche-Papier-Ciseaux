package cgg.informatique.abl.webSocket.dao;

import cgg.informatique.abl.webSocket.dto.CompteDto;
import cgg.informatique.abl.webSocket.entites.Compte;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class CompteServiceImpl implements CompteService {
    private CompteDao compteDao;

    public CompteServiceImpl(
            @Autowired CompteDao compteDao) {
        this.compteDao = compteDao;
    }

    @Override
    @Transactional
    public Compte registerNewUserAccount(CompteDto compteDto) throws IllegalArgumentException {
        if (compteDao.existsByCourriel(compteDto.getCourriel())) throw new IllegalArgumentException("courriel existe");
        if (compteDao.existsByAlias(compteDto.getAlias())) throw new IllegalArgumentException("alias existe");

        Compte compte = Compte.Builder()
                .avecCourriel(compteDto.getCourriel())
                .avecMotDePasse(new BCryptPasswordEncoder().encode(compteDto.getPassword()))
                .avecAlias(compteDto.getAlias())
                .avecAvatar(compteDto.getAvatar())
                .build();

        return compteDao.save(compte);
    }
}
