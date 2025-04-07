package org.leralix.tan.listeners.chat.events;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.factory.AbstractionFactory;
import org.leralix.tan.storage.stored.TownDataStorage;

import static org.junit.jupiter.api.Assertions.*;

class DonateToTerritoryTest {

    @BeforeAll
    static void setUp() {
        AbstractionFactory.initializeConfigs();
    }

    @Test
    void nominalCase() {
        var playerData = AbstractionFactory.getRandomPlayerData();
        TownData townData = TownDataStorage.getInstance().newTown("townToDonate", playerData);
        int amount = 1;
        playerData.addToBalance(amount);

        DonateToTerritory donateToTerritory = new DonateToTerritory(townData);
        donateToTerritory.execute(playerData.getPlayer(), String.valueOf(amount));

        assertEquals(amount, townData.getBalance());
    }

    @Test
    void notEnoughMoney() {
        var playerData = AbstractionFactory.getRandomPlayerData();
        TownData townData = TownDataStorage.getInstance().newTown("townToDonate", playerData);

        int amount = (int) (playerData.getBalance() + 1);

        DonateToTerritory donateToTerritory = new DonateToTerritory(townData);
        donateToTerritory.execute(playerData.getPlayer(), String.valueOf(amount));

        assertEquals(0, townData.getBalance());
    }

    @Test
    void notANumber() {
        var playerData = AbstractionFactory.getRandomPlayerData();
        TownData townData = TownDataStorage.getInstance().newTown("townToDonate", playerData);

        String amount = "notANumber";

        DonateToTerritory donateToTerritory = new DonateToTerritory(townData);
        donateToTerritory.execute(playerData.getPlayer(), amount);

        assertEquals(0, townData.getBalance());
    }


}