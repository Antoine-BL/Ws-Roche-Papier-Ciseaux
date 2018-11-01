package cgg.informatique.abl.webSocket.controleurs.mvc;

import cgg.informatique.abl.webSocket.dao.CompteDao;
import cgg.informatique.abl.webSocket.entites.Compte;
import cgg.informatique.abl.webSocket.entites.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class MainController {

    private CompteDao compteDao;

    public MainController(@Autowired CompteDao compteDao) {
        this.compteDao = compteDao;
    }

    @GetMapping("/")
    public String index(@Autowired Authentication auth, Model model) {
        return "index";
    }

    @GetMapping("/ecole")
    public String ecole(@Autowired Authentication auth) {
        return "ecole";
    }

    @GetMapping("/kumite")
    public String kumite(@Autowired Authentication auth, Model model) {
        return "kumite";
    }

    @GetMapping("/passage")
    public String passage(@Autowired Authentication auth, Model model) {
        return "passage";
    }
}
