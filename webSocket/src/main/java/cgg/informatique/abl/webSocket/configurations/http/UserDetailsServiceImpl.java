package cgg.informatique.abl.webSocket.configurations.http;

import cgg.informatique.abl.webSocket.dao.CompteDao;
import cgg.informatique.abl.webSocket.entites.Compte;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Qualifier("compte")
class UserDetailsServiceImpl implements UserDetailsService {
    private CompteDao compteDao;

    public UserDetailsServiceImpl(@Autowired CompteDao compteDao) {
        this.compteDao = compteDao;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        Optional<Compte> compte = compteDao.findByCourriel(email);

        return new UserDetailsImpl(
                compte.orElseThrow(() -> new UsernameNotFoundException(email))
        );
    }
}
