package cgg.informatique.abl.webSocket.dto;

import cgg.informatique.abl.webSocket.configurations.StompSessionHandlerImpl;
import cgg.informatique.abl.webSocket.entites.Compte;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Predicate;

public class Match {
    private static final String URL = "";
    private static final int PING_DELAY = 1000;

    private boolean commence;
    private List<Compte> spectateurs;
    private List<Compte> combattants;
    private Compte arbitre;
    private Compte combattantG;
    private Compte combattantD;
    private WebSocketStompClient stompClient;
    private StompSession stompSession;

    public Match() {
        spectateurs = new ArrayList<>();
        combattants = new ArrayList<>();
        initStompClient();
    }

    private void initStompClient() {
        WebSocketClient client = new StandardWebSocketClient();
        stompClient = new WebSocketStompClient(client);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
    }

    private void debuter() throws ExecutionException, InterruptedException {
        commence = true;

        stompSession = stompClient.connect(URL, new StompSessionHandlerImpl()).get();
    }

    private void mainLoop() {
        while(commence) {

        }
    }

    private void terminer() {
        commence = false;
    }

    private void ajouterSpectateur(Compte spectateur) {
        spectateurs.add(spectateur);

        if (isAlreadyPresent(spectateur)) throw new IllegalArgumentException("Spectateur déjà présent");
    }

    private void enleverSpectateur(Compte spectateur) {
        Predicate<? super Compte> isPresent = generateMatcher(spectateur);

        if (!spectateurs.removeIf(isPresent)) {
            throw new IllegalArgumentException("spectateur est absent.");
        }
    }

    private void ajouterCombattant(Compte spectateur) {
        spectateurs.add(spectateur);

        if (isAlreadyPresent(spectateur)) throw new IllegalArgumentException("Spectateur déjà présent");
    }

    private void enleverCombattant(Compte spectateur) {
        Predicate<? super Compte> isPresent = generateMatcher(spectateur);

        if (!combattants.removeIf(isPresent)) {
            throw new IllegalArgumentException("spectateur est absent.");
        }
    }

    private boolean isAlreadyPresent(Compte compte){
        Predicate<? super Compte> isPresent = generateMatcher(compte);

        if (spectateurs.stream().anyMatch(isPresent)) return true;
        if (combattants.stream().anyMatch(isPresent)) return true;
        return isPresent.test(combattantG)
                || isPresent.test(combattantD)
                || isPresent.test(arbitre);
    }

    private Predicate<? super Compte> generateMatcher(Compte compte) {
        return spec -> spec.getCourriel().equals(compte.getCourriel());
    }

    public boolean isCommence() {
        return commence;
    }

    public void setIsCommence(boolean estCommence) {
        this.commence = estCommence;
    }

    public Compte getArbitre() {
        return arbitre;
    }

    public void setArbitre(Compte arbitre) {
        this.arbitre = arbitre;
    }

    public Compte getCombattantG() {
        return combattantG;
    }

    public void setCombattantG(Compte combattantG) {
        this.combattantG = combattantG;
    }

    public Compte getCombattantD() {
        return combattantD;
    }

    public void setCombattantD(Compte combattantD) {
        this.combattantD = combattantD;
    }
}
