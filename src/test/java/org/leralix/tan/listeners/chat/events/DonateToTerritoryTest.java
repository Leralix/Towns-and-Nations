package org.leralix.tan.listeners.chat.events;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.factory.AbstractionFactory;
import org.leralix.tan.storage.stored.TownDataStorage;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DonateToTerritoryTest {

    @BeforeAll
    static void setUp() {
        AbstractionFactory.initializeConfigs();
    }

    @Test
    void nominalCase() {
        var tanPlayer = AbstractionFactory.getRandomITanPlayer();
        TownData townData = TownDataStorage.getInstance().newTown("townToDonate", tanPlayer);
        int amount = 1;
        tanPlayer.addToBalance(amount);

        DonateToTerritory donateToTerritory = new DonateToTerritory(townData);
        donateToTerritory.execute(tanPlayer.getPlayer(), String.valueOf(amount));

        assertEquals(amount, townData.getBalance());
    }

    @Test
    void notEnoughMoney() {
        var tanPlayer = AbstractionFactory.getRandomITanPlayer();
        TownData townData = TownDataStorage.getInstance().newTown("townToDonate", tanPlayer);

        int amount = (int) (tanPlayer.getBalance() + 1);

        DonateToTerritory donateToTerritory = new DonateToTerritory(townData);
        donateToTerritory.execute(tanPlayer.getPlayer(), String.valueOf(amount));

        assertEquals(0, townData.getBalance());
    }

    @Test
    void notANumber() {
        var tanPlayer = AbstractionFactory.getRandomITanPlayer();
        TownData townData = TownDataStorage.getInstance().newTown("townToDonate", tanPlayer);

        String amount = "notANumber";

        DonateToTerritory donateToTerritory = new DonateToTerritory(townData);
        donateToTerritory.execute(tanPlayer.getPlayer(), amount);

        assertEquals(0, townData.getBalance());
    }


}