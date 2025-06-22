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
        var ITanPlayer = AbstractionFactory.getRandomITanPlayer();
        TownData townData = TownDataStorage.getInstance().newTown("townToDonate", ITanPlayer);
        int amount = 1;
        ITanPlayer.addToBalance(amount);

        DonateToTerritory donateToTerritory = new DonateToTerritory(townData);
        donateToTerritory.execute(ITanPlayer.getPlayer(), String.valueOf(amount));

        assertEquals(amount, townData.getBalance());
    }

    @Test
    void notEnoughMoney() {
        var ITanPlayer = AbstractionFactory.getRandomITanPlayer();
        TownData townData = TownDataStorage.getInstance().newTown("townToDonate", ITanPlayer);

        int amount = (int) (ITanPlayer.getBalance() + 1);

        DonateToTerritory donateToTerritory = new DonateToTerritory(townData);
        donateToTerritory.execute(ITanPlayer.getPlayer(), String.valueOf(amount));

        assertEquals(0, townData.getBalance());
    }

    @Test
    void notANumber() {
        var ITanPlayer = AbstractionFactory.getRandomITanPlayer();
        TownData townData = TownDataStorage.getInstance().newTown("townToDonate", ITanPlayer);

        String amount = "notANumber";

        DonateToTerritory donateToTerritory = new DonateToTerritory(townData);
        donateToTerritory.execute(ITanPlayer.getPlayer(), amount);

        assertEquals(0, townData.getBalance());
    }


}