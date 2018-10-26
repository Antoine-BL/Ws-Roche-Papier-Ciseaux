package cgg.informatique.abl.webSocket.messaging.commands;

import cgg.informatique.abl.webSocket.entites.Compte;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CommandDeserializer extends StdDeserializer<Commande> {
    public CommandDeserializer() {
        this(null);
    }

    public CommandDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Commande deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        ObjectNode node = jp.getCodec().readTree(jp);
        JsonNode typeNode = node.get("typeCommande");

        if (typeNode == null || !typeNode.isTextual()) throw new IOException("Invalid JSON, typeCommande is not textual");

        String type = typeNode.textValue();
        try {
            TypeCommande typeCommande = TypeCommande.valueOf(type);
            node.remove("typeCommande");

            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true);

            return mapper.treeToValue(node, typeCommande.getMappedSubtype());
        } catch (IllegalArgumentException e) {
            return new Error(type);
        }
    }
}
