package org.leralix.tan.listeners.chat.events;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.dataclass.PlayerData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.factory.AbstractionFactory;

import static org.junit.jupiter.api.Assertions.*;

class CreateTownTest {

    @BeforeAll
    static void setUp() {
        AbstractionFactory.initializeConfigs();
    }

    @Test
    void nominalCase() {

        PlayerData playerData = AbstractionFactory.getRandomPlayerData();

        CreateTown createTown = new CreateTown(10);
        createTown.execute(playerData.getPlayer(), "town-A");

        assertTrue(playerData.hasTown());
        TownData town = playerData.getTown();
        assertEquals(1, town.getAllRanks().size());
        assertEquals(1, town.getTownDefaultRank().getNumberOfPlayer());
        assertEquals(0, town.getBalance());
        assertEquals(1, town.getPlayerIDList().size());
    }

    @Test
    void notEnoughMoney() {

        PlayerData playerData = AbstractionFactory.getRandomPlayerData();

        CreateTown createTown = new CreateTown((int) (playerData.getBalance() + 1));
        createTown.execute(playerData.getPlayer(), "anotherName");

        assertFalse(playerData.hasTown());
    }

    @Test
    void nameTooLong() {

        PlayerData playerData = AbstractionFactory.getRandomPlayerData();

        int maxSize = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("RegionNameSize");

        CreateTown createTown = new CreateTown(0);
        createTown.execute(playerData.getPlayer(), "a" + "a".repeat(Math.max(0, maxSize)));

        assertFalse(playerData.hasTown());
    }

    @Test
    void nameAlreadyUsed() {

        PlayerData playerData1 = AbstractionFactory.getRandomPlayerData();
        PlayerData playerData2 = AbstractionFactory.getRandomPlayerData();

        String townName = "townWithDuplicateName";

        CreateTown createTown = new CreateTown(0);
        createTown.execute(playerData1.getPlayer(), townName);
        createTown.execute(playerData2.getPlayer(), townName);

        assertTrue(playerData1.hasTown());
        assertFalse(playerData2.hasTown());
    }

}