package org.leralix.tan.listeners.chat.events;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.factory.AbstractionFactory;
import org.leralix.tan.storage.stored.TownDataStorage;

import static org.junit.jupiter.api.Assertions.*;

class CreateRegionTest {


    @BeforeAll
    static void setUp() {
        AbstractionFactory.initializeConfigs();
    }

    @Test
    void nominalCase(){
        var playerData = AbstractionFactory.getRandomPlayerData();
        var townData = TownDataStorage.getInstance().newTown("Town-B", playerData);
        townData.addToBalance(50);
        String regionName = "Region-B";

        CreateRegion createRegion = new CreateRegion(25);
        createRegion.execute(playerData.getPlayer(), regionName);

        assertTrue(townData.haveOverlord());
        RegionData regionData = townData.getRegion();
        assertFalse(regionData.haveOverlord());
        assertEquals(regionName, regionData.getName());
        assertEquals(1, regionData.getSubjects().size());
        assertEquals(25, townData.getBalance());
    }

    @Test
    void playerNotLeader(){
        var playerData = AbstractionFactory.getRandomPlayerData();
        var secondPlayerData = AbstractionFactory.getRandomPlayerData();

        var townData = TownDataStorage.getInstance().newTown("Town", playerData);

        townData.addPlayer(secondPlayerData);

        String regionName = "Region";

        CreateRegion createRegion = new CreateRegion(0);
        createRegion.execute(secondPlayerData.getPlayer(), regionName);

        assertFalse(townData.haveOverlord());
    }

    @Test
    void notEnoughMoney(){
        var playerData = AbstractionFactory.getRandomPlayerData();

        var townData = TownDataStorage.getInstance().newTown("Town", playerData);

        CreateRegion createRegion = new CreateRegion(1);
        createRegion.execute(playerData.getPlayer(), "Region");

        assertFalse(townData.haveOverlord());
    }

    @Test
    void regionNameTooLong(){
        var playerData = AbstractionFactory.getRandomPlayerData();
        var townData = TownDataStorage.getInstance().newTown("Town", playerData);
        townData.addToBalance(50);

        int maxSize = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("RegionNameSize");

        CreateRegion createRegion = new CreateRegion(25);
        createRegion.execute(playerData.getPlayer(), "a" + "a".repeat(Math.max(0, maxSize)));

        assertFalse(townData.haveOverlord());
    }

    @Test
    void regionNameAlreadyUsed(){
        var playerData1 = AbstractionFactory.getRandomPlayerData();
        var townData1 = TownDataStorage.getInstance().newTown("townData1", playerData1);

        var playerData2 = AbstractionFactory.getRandomPlayerData();
        var townData2 = TownDataStorage.getInstance().newTown("townData2", playerData2);

        String regionName = "specificRegionName";

        CreateRegion createRegion = new CreateRegion(0);
        createRegion.execute(playerData1.getPlayer(), regionName);
        createRegion.execute(playerData2.getPlayer(), regionName);

        assertTrue(townData1.haveOverlord());
        assertFalse(townData2.haveOverlord());
    }



}