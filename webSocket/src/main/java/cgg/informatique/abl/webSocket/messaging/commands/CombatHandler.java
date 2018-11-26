package cgg.informatique.abl.webSocket.messaging.commands;

import cgg.informatique.abl.webSocket.controleurs.rest.CompteController;
import cgg.informatique.abl.webSocket.controleurs.webSocket.FightController;
import cgg.informatique.abl.webSocket.entites.Combat;
import cgg.informatique.abl.webSocket.entites.Compte;
import cgg.informatique.abl.webSocket.game.lobby.Lobby;
import cgg.informatique.abl.webSocket.game.lobby.LobbyRole;
import cgg.informatique.abl.webSocket.game.lobby.LobbyUserData;
import cgg.informatique.abl.webSocket.messaging.DonneesReponseCommande;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize()
public class CombatHandler extends Commande {
    @Override
    public void execute(LobbyCommandContext context) {
        try {
            Compte compte =  getDe();
            if (compte.equals(CompteController.SENSEI1) || compte.equals(CompteController.VENERABLE))
                throw new Exception("Ne pêut pâs être s1@dojo ou v1@dojo. S'il vous plaît choisir un autre combattant");

            Lobby lobby = FightController.getLobby();
            LobbyUserData lobbyUser = lobby.getLobbyUserData(compte);
            String resultat = getParametres().get(0);

            Combat.ResultatStep combatBuilder;
            switch (resultat) {
                case "ROUGE":
                    if (lobbyUser.getRoleCombat() != LobbyRole.COMBATTANT) throw new Exception("Doit être combattant!");
                    combatBuilder = combattant(compte);

                    lobby.saveMatch(combatBuilder.setResultat(10, 0, 1).build());
                    break;
                case "BLANC":
                    if (lobbyUser.getRoleCombat() != LobbyRole.COMBATTANT) throw new Exception("Doit être combattant!");
                    combatBuilder = combattant(compte);

                    lobby.saveMatch(combatBuilder.setResultat(0, 10, 1).build());
                    break;
                case "NUL":
                    if (lobbyUser.getRoleCombat() != LobbyRole.COMBATTANT) throw new Exception("Doit être combattant!");
                    combatBuilder = combattant(compte);

                    lobby.saveMatch(combatBuilder.setResultat(5, 5, 1).build());
                    break;
                case "ARBITRE":
                    if (lobbyUser.getRoleCombat() != LobbyRole.ARBITRE) throw new Exception("Doit être arbitre!");
                    combatBuilder = arbitre(compte);

                    lobby.saveMatch(combatBuilder.setResultat(10, 0, 1).build());
                    break;
                case "FAUTE":
                    if (lobbyUser.getRoleCombat() != LobbyRole.ARBITRE) throw new Exception("Doit être arbitre!");
                    combatBuilder = arbitre(compte);

                    lobby.saveMatch(combatBuilder.setResultat(5, 5, 0).build());
                    break;
            }

            Compte cmpt = lobby.getUser(compte.getCourriel());
            Integer points = CompteController.getPointsPour(cmpt);
            Integer credits = CompteController.getCreditsPour(cmpt);
            lobby.sendData("asd", new DonneesReponseCommande(TypeCommande.COMBAT, lobbyUser, points.toString(), credits.toString()));
        } catch (Exception e) {
            FightController.getLobby().sendData("Échec en créant le combat: " + e.getMessage(), new DonneesReponseCommande(TypeCommande.ERREUR));
        }
    }

    private Combat.ResultatStep combattant(Compte utilisateurCourant) {
        return Combat.Builder()
                .setRouge(utilisateurCourant)
                .setBlanc(CompteController.SENSEI1)
                .setArbitre(CompteController.VENERABLE);
    }

    private Combat.ResultatStep arbitre(Compte utilisateurCourant) {
        return Combat.Builder()
                .setRouge(CompteController.VENERABLE)
                .setBlanc(CompteController.SENSEI1)
                .setArbitre(utilisateurCourant);
    }
}
