package org.leralix.tan.listeners.chat.events;

import org.junit.jupiter.api.Test;
import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.BasicTest;
import org.leralix.tan.data.territory.RegionData;
import org.leralix.tan.storage.stored.TownDataStorage;

import static org.junit.jupiter.api.Assertions.*;

class CreateRegionTest extends BasicTest {

    @Test
    void nominalCase(){
        var tanPlayer = townsAndNations.getPlayerDataStorage().get(server.addPlayer());
        var townData = TownDataStorage.getInstance().newTown("Town-B", tanPlayer);
        townData.addToBalance(50);
        String regionName = "Region-B";

        CreateRegion createRegion = new CreateRegion(25);
        createRegion.execute(tanPlayer.getPlayer(), regionName);

        assertTrue(townData.haveOverlord());
        assertTrue(townData.getRegion().isPresent());
        RegionData regionData = townData.getRegion().get();
        assertFalse(regionData.haveOverlord());
        assertEquals(regionName, regionData.getName());
        assertEquals(1, regionData.getSubjects().size());
        assertEquals(25, townData.getBalance());
    }

    @Test
    void playerNotLeader(){
        var tanPlayer = townsAndNations.getPlayerDataStorage().get(server.addPlayer());
        var secondTanPlayer = townsAndNations.getPlayerDataStorage().get(server.addPlayer());

        var townData = TownDataStorage.getInstance().newTown("Town", tanPlayer);

        townData.addPlayer(secondTanPlayer);

        String regionName = "Region";

        CreateRegion createRegion = new CreateRegion(0);
        createRegion.execute(secondTanPlayer.getPlayer(), regionName);

        assertFalse(townData.haveOverlord());
    }

    @Test
    void notEnoughMoney(){
        var tanPlayer = townsAndNations.getPlayerDataStorage().get(server.addPlayer());

        var townData = TownDataStorage.getInstance().newTown("Town", tanPlayer);

        CreateRegion createRegion = new CreateRegion(1);
        createRegion.execute(tanPlayer.getPlayer(), "Region");

        assertFalse(townData.haveOverlord());
    }

    @Test
    void regionNameTooLong(){
        var tanPlayer = townsAndNations.getPlayerDataStorage().get(server.addPlayer());
        var townData = TownDataStorage.getInstance().newTown("Town", tanPlayer);
        townData.addToBalance(50);

        int maxSize = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("RegionNameSize");

        CreateRegion createRegion = new CreateRegion(25);
        createRegion.execute(tanPlayer.getPlayer(), "a" + "a".repeat(Math.max(0, maxSize)));

        assertFalse(townData.haveOverlord());
    }

    @Test
    void regionNameAlreadyUsed(){
        var tanPlayer1 = townsAndNations.getPlayerDataStorage().get(server.addPlayer());
        var townData1 = TownDataStorage.getInstance().newTown("townData1", tanPlayer1);

        var tanPlayer2 = townsAndNations.getPlayerDataStorage().get(server.addPlayer());
        var townData2 = TownDataStorage.getInstance().newTown("townData2", tanPlayer2);

        String regionName = "specificRegionName";

        CreateRegion createRegion = new CreateRegion(0);
        createRegion.execute(tanPlayer1.getPlayer(), regionName);
        createRegion.execute(tanPlayer2.getPlayer(), regionName);

        assertTrue(townData1.haveOverlord());
        assertFalse(townData2.haveOverlord());
    }



}