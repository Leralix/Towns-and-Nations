package org.leralix.tan.api.external.papi.entries;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.leralix.tan.BasicTest;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.RegionData;
import org.leralix.tan.data.territory.TownData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.RegionDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;

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
        ITanPlayer tanPlayer = townsAndNations.getPlayerDataStorage().get(player);

        TownData townData = TownDataStorage.getInstance().newTown("Town", tanPlayer);
        RegionData regionData = RegionDataStorage.getInstance().createNewRegion("Region", townData);

        OtherPlayerRegionName entry = new OtherPlayerRegionName(townsAndNations.getPlayerDataStorage());

        String name = entry.getData(player, "player_{player name}_region_name");

        assertEquals(regionData.getName(), name);
    }

    @Test
    void noRegionTest() {

        Player player = server.addPlayer("player name");
        townsAndNations.getPlayerDataStorage().get(player);

        OtherPlayerRegionName entry = new OtherPlayerRegionName(townsAndNations.getPlayerDataStorage());

        String name = entry.getData(player, "player_{player name}_region_name");

        assertEquals(Lang.NO_REGION.get(player), name);
    }

}