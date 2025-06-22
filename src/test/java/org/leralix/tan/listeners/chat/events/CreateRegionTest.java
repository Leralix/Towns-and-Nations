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
        var ITanPlayer = AbstractionFactory.getRandomITanPlayer();
        var townData = TownDataStorage.getInstance().newTown("Town-B", ITanPlayer);
        townData.addToBalance(50);
        String regionName = "Region-B";

        CreateRegion createRegion = new CreateRegion(25);
        createRegion.execute(ITanPlayer.getPlayer(), regionName);

        assertTrue(townData.haveOverlord());
        RegionData regionData = townData.getRegion();
        assertFalse(regionData.haveOverlord());
        assertEquals(regionName, regionData.getName());
        assertEquals(1, regionData.getSubjects().size());
        assertEquals(25, townData.getBalance());
    }

    @Test
    void playerNotLeader(){
        var ITanPlayer = AbstractionFactory.getRandomITanPlayer();
        var secondITanPlayer = AbstractionFactory.getRandomITanPlayer();

        var townData = TownDataStorage.getInstance().newTown("Town", ITanPlayer);

        townData.addPlayer(secondITanPlayer);

        String regionName = "Region";

        CreateRegion createRegion = new CreateRegion(0);
        createRegion.execute(secondITanPlayer.getPlayer(), regionName);

        assertFalse(townData.haveOverlord());
    }

    @Test
    void notEnoughMoney(){
        var ITanPlayer = AbstractionFactory.getRandomITanPlayer();

        var townData = TownDataStorage.getInstance().newTown("Town", ITanPlayer);

        CreateRegion createRegion = new CreateRegion(1);
        createRegion.execute(ITanPlayer.getPlayer(), "Region");

        assertFalse(townData.haveOverlord());
    }

    @Test
    void regionNameTooLong(){
        var ITanPlayer = AbstractionFactory.getRandomITanPlayer();
        var townData = TownDataStorage.getInstance().newTown("Town", ITanPlayer);
        townData.addToBalance(50);

        int maxSize = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("RegionNameSize");

        CreateRegion createRegion = new CreateRegion(25);
        createRegion.execute(ITanPlayer.getPlayer(), "a" + "a".repeat(Math.max(0, maxSize)));

        assertFalse(townData.haveOverlord());
    }

    @Test
    void regionNameAlreadyUsed(){
        var ITanPlayer1 = AbstractionFactory.getRandomITanPlayer();
        var townData1 = TownDataStorage.getInstance().newTown("townData1", ITanPlayer1);

        var ITanPlayer2 = AbstractionFactory.getRandomITanPlayer();
        var townData2 = TownDataStorage.getInstance().newTown("townData2", ITanPlayer2);

        String regionName = "specificRegionName";

        CreateRegion createRegion = new CreateRegion(0);
        createRegion.execute(ITanPlayer1.getPlayer(), regionName);
        createRegion.execute(ITanPlayer2.getPlayer(), regionName);

        assertTrue(townData1.haveOverlord());
        assertFalse(townData2.haveOverlord());
    }



}