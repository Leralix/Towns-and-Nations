package org.leralix.tan.listeners.chat.events;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.dataclass.ITanPlayer;
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

        ITanPlayer ITanPlayer = AbstractionFactory.getRandomITanPlayer();

        CreateTown createTown = new CreateTown(10);
        createTown.execute(ITanPlayer.getPlayer(), "town-A");

        assertTrue(ITanPlayer.hasTown());
        TownData town = ITanPlayer.getTown();
        assertEquals(1, town.getAllRanks().size());
        assertEquals(1, town.getTownDefaultRank().getNumberOfPlayer());
        assertEquals(0, town.getBalance());
        assertEquals(1, town.getPlayerIDList().size());
    }

    @Test
    void notEnoughMoney() {

        ITanPlayer ITanPlayer = AbstractionFactory.getRandomITanPlayer();

        CreateTown createTown = new CreateTown((int) (ITanPlayer.getBalance() + 1));
        createTown.execute(ITanPlayer.getPlayer(), "anotherName");

        assertFalse(ITanPlayer.hasTown());
    }

    @Test
    void nameTooLong() {

        ITanPlayer ITanPlayer = AbstractionFactory.getRandomITanPlayer();

        int maxSize = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("RegionNameSize");

        CreateTown createTown = new CreateTown(0);
        createTown.execute(ITanPlayer.getPlayer(), "a" + "a".repeat(Math.max(0, maxSize)));

        assertFalse(ITanPlayer.hasTown());
    }

    @Test
    void nameAlreadyUsed() {

        ITanPlayer ITanPlayer1 = AbstractionFactory.getRandomITanPlayer();
        ITanPlayer ITanPlayer2 = AbstractionFactory.getRandomITanPlayer();

        String townName = "townWithDuplicateName";

        CreateTown createTown = new CreateTown(0);
        createTown.execute(ITanPlayer1.getPlayer(), townName);
        createTown.execute(ITanPlayer2.getPlayer(), townName);

        assertTrue(ITanPlayer1.hasTown());
        assertFalse(ITanPlayer2.hasTown());
    }

}