package cgg.informatique.abl.webSocket.messaging;

import cgg.informatique.abl.webSocket.messaging.commands.Debuter;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public enum TypeCommande {
    DEBUTER(Debuter.class);

    private Class<? extends Commande> mappedSubtype;

    TypeCommande(Class<? extends Commande> mappedSubtype) {
        this.mappedSubtype = mappedSubtype;
    }

    public Commande construct(List<String> params)
            throws NoSuchMethodException, IllegalAccessException,
                   InvocationTargetException, InstantiationException {
        Constructor<? extends Commande> constructor = mappedSubtype
                                                        .getConstructor(List.class);

        return constructor.newInstance(params);
    }
}

