package cgg.informatique.abl.webSocket.dto.lobby;

import cgg.informatique.abl.webSocket.dto.UserBase;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

public class RoleColl{
    private static final int LENGTH = 12;

    private LobbyUserData[] contents;
    private LobbyRole role;

    public RoleColl(LobbyRole role) {
        contents = new LobbyUserData[LENGTH];
        this.role = role;
    }

    public LobbyUserData[] getNonNull() {
        return Arrays
                .stream(contents)
                .filter(Objects::nonNull)
                .toArray(LobbyUserData[]::new);
    }

    public int size(){
        return getNonNull().length;
    }

    public LobbyPosition addUser(LobbyUserData user) {
        int firstFree = getFirstFreeSpot();

        contents[firstFree] = user;

        return new LobbyPosition(role, firstFree);
    }

    public LobbyPosition addUserAt(LobbyUserData user, int index) {
        if (contents[index] != null) throw new IllegalStateException("Il y a déjà un " + role + " qui occupe cette position");

        contents[index] = user;
        user.setRole(role);

        return new LobbyPosition(role, index);
    }

    private int getFirstFreeSpot() {
        return getFreeIndices()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Il n'y a aucune place " + role + " de disponible"));
    }

    private IntStream getFreeIndices() {
        return IntStream.range(0, contents.length)
                .filter(i -> contents[i] == null);
    }


    public int getRandomSpot() {
        int[] freeIndices = getFreeIndices().toArray();

        if (freeIndices.length == 0) throw new IllegalStateException("Il n'y a aucune place " + role + " de disponible");

        Random rdm = ThreadLocalRandom.current();
        int randomIndex = rdm.nextInt(freeIndices.length);

        return freeIndices[randomIndex];
    }

    public LobbyPosition removeUser(LobbyUserData user) {
        int userPos = getUserPos(user);

        contents[userPos] = null;

        return new LobbyPosition(role, userPos);
    }

    public int getUserPos(LobbyUserData user) {
        int[] indices = IntStream
                .range(0, contents.length)
                .filter(i -> contents[i] != null)
                .filter(i -> contents[i].equals(user)).toArray();

        if (indices.length == 0) throw new IllegalArgumentException("Cet utilisateur n'est pas présent dans la liste des " + role );
        if (indices.length > 1) throw new IllegalStateException("Il existe un doublon dans cette liste");

        return indices[0];
    }

    public LobbyUserData[] getContents() {
        return contents;
    }

    public void checkAvailable(Integer position) {
        if (position == null) {
            getFirstFreeSpot();
        } else {
            if (contents[position] != null) throw new IllegalStateException("Il y a déjà un " + role + " qui occupe cette position");
        }
    }
}
