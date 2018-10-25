package cgg.informatique.abl.webSocket.messaging;

import cgg.informatique.abl.webSocket.messaging.commands.Error;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
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
        JsonNode node = jp.getCodec().readTree(jp);
        JsonNode typeNode = node.get("typeCommande");
        JsonNode parametersNode = node.get("parametres");

        if (!typeNode.isTextual()) throw new IOException("Invalid JSON, typeCommande is not textual");
        if (!parametersNode.isArray()) throw new IOException("Invalid JSON, parameters not an array");

        String type = typeNode.textValue();
        Iterator<JsonNode> itParamsJson = parametersNode.elements();

        List<String> params = new ArrayList<>();

        while(itParamsJson.hasNext()) {
            params.add(itParamsJson.next().textValue());
        }

        try {
            return TypeCommande.valueOf(type).construct(params);
        } catch (IllegalArgumentException e) {
            return new Error(type);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Error creating Command class", e);
        }
    }
}
