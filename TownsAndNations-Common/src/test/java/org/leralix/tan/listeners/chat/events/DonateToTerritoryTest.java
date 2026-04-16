package org.leralix.tan.listeners.chat.events;

import org.junit.jupiter.api.Test;
import org.leralix.tan.BasicTest;
import org.leralix.tan.data.territory.Town;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DonateToTerritoryTest extends BasicTest {

    @Test
    void nominalCase() {
        var tanPlayer = playerDataStorage.get(server.addPlayer());
        Town townData = townStorage.newTown("townToDonate", tanPlayer);
        int amount = 1;
        tanPlayer.addToBalance(amount);

        DonateToTerritory donateToTerritory = new DonateToTerritory(townData);
        donateToTerritory.execute(tanPlayer.getPlayer(), tanPlayer, String.valueOf(amount));

        assertEquals(amount, townData.getBalance());
    }

    @Test
    void notEnoughMoney() {
        var tanPlayer = playerDataStorage.get(server.addPlayer());
        Town townData = townStorage.newTown("townToDonate", tanPlayer);

        int amount = (int) (tanPlayer.getBalance() + 1);

        DonateToTerritory donateToTerritory = new DonateToTerritory(townData);
        donateToTerritory.execute(tanPlayer.getPlayer(), tanPlayer, String.valueOf(amount));

        assertEquals(0, townData.getBalance());
    }

    @Test
    void notANumber() {
        var tanPlayer = playerDataStorage.get(server.addPlayer());
        Town townData = townStorage.newTown("townToDonate", tanPlayer);

        String amount = "notANumber";

        DonateToTerritory donateToTerritory = new DonateToTerritory(townData);
        donateToTerritory.execute(tanPlayer.getPlayer(), tanPlayer, amount);

        assertEquals(0, townData.getBalance());
    }


}