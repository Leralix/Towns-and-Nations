package org.leralix.tan.listeners.chat.events;

import org.junit.jupiter.api.Test;
import org.leralix.tan.BasicTest;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.TownData;
import org.leralix.tan.utils.constants.Constants;

import static org.junit.jupiter.api.Assertions.*;

class CreateTownTest extends BasicTest {
    
    @Test
    void nominalCase() {

        ITanPlayer tanPlayer = playerDataStorage.get(server.addPlayer());

        CreateTown createTown = new CreateTown(10);
        createTown.execute(tanPlayer.getPlayer(),tanPlayer, "town-A");

        assertTrue(tanPlayer.hasTown());
        TownData town = tanPlayer.getTown();
        assertEquals(1, town.getAllRanks().size());
        assertEquals(1, town.getTownDefaultRank().getNumberOfPlayer());
        assertEquals(0, town.getBalance());
        assertEquals(1, town.getPlayerIDList().size());
    }

    @Test
    void notEnoughMoney() {

        ITanPlayer tanPlayer = playerDataStorage.get(server.addPlayer());

        CreateTown createTown = new CreateTown((int) (tanPlayer.getBalance() + 1));
        createTown.execute(tanPlayer.getPlayer(), tanPlayer, "anotherName");

        assertFalse(tanPlayer.hasTown());
    }

    @Test
    void nameTooLong() {

        ITanPlayer tanPlayer = playerDataStorage.get(server.addPlayer());

        int maxSize = Constants.getTownMaxNameSize();

        CreateTown createTown = new CreateTown(0);
        createTown.execute(tanPlayer.getPlayer(), tanPlayer, "a" + "a".repeat(Math.max(0, maxSize)));

        assertFalse(tanPlayer.hasTown());
    }

    @Test
    void nameAlreadyUsed() {

        ITanPlayer tanPlayer1 = playerDataStorage.get(server.addPlayer());
        ITanPlayer tanPlayer2 = playerDataStorage.get(server.addPlayer());

        String townName = "townWithDuplicateName";

        CreateTown createTown = new CreateTown(0);
        createTown.execute(tanPlayer1.getPlayer(), tanPlayer1, townName);
        createTown.execute(tanPlayer2.getPlayer(), tanPlayer2, townName);

        assertTrue(tanPlayer1.hasTown());
        assertFalse(tanPlayer2.hasTown());
    }

}