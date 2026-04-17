package org.leralix.tan.api.external.papi.entries;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.leralix.tan.BasicTest;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.Region;
import org.leralix.tan.data.territory.Town;
import org.leralix.tan.lang.Lang;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OtherPlayerRegionNameTest extends BasicTest {

    @Override
    @BeforeEach
    protected void setUp() {
        super.setUp();
    }


    @Test
    void nominalTest() {

        Player player = server.addPlayer("player name");
        ITanPlayer tanPlayer = playerDataStorage.get(player);

        Town townData = townStorage.newTown("Town", tanPlayer);
        Region regionData = regionStorage.newRegion("Region", townData);

        OtherPlayerRegionName entry = new OtherPlayerRegionName(playerDataStorage, townStorage, null, null);

        String name = entry.getData(player, "player_{player name}_region_name");

        assertEquals(regionData.getName(), name);
    }

    @Test
    void noRegionTest() {

        Player player = server.addPlayer("player name");
        playerDataStorage.get(player);

        OtherPlayerRegionName entry = new OtherPlayerRegionName(playerDataStorage, townStorage, null, null);

        String name = entry.getData(player, "player_{player name}_region_name");

        assertEquals(Lang.NO_REGION.get(langType), name);
    }

}