package cgg.informatique.abl.webSocket.dto;

import java.util.Objects;

public abstract class UserBase {
    public abstract String getCourriel();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !UserBase.class.isAssignableFrom(o.getClass())) return false;
        UserBase userBase = (UserBase) o;
        return Objects.equals(getCourriel(), userBase.getCourriel());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCourriel());
    }
}
