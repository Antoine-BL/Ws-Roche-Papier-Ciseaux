package cgg.informatique.abl.webSocket.messaging.commands;

import cgg.informatique.abl.webSocket.controleurs.webSocket.FightController;
import cgg.informatique.abl.webSocket.messaging.Reponse;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.apache.commons.compress.archivers.dump.DumpArchiveEntry;

import java.lang.reflect.Type;
import java.util.List;

@JsonDeserialize()
public class LobbyMessage extends Commande{
    public LobbyMessage() {

    }

    public LobbyMessage(List<String> params) {
        super(params);
        typeCommande = TypeCommande.LOBBYMESSAGE;
    }

    @Override
    public Reponse execute(FightController context) {
        return new Reponse(1L, parametres.get(0));
    }
}
