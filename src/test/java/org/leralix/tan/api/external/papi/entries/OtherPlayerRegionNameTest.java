package org.leralix.tan.api.external.papi.entries;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.leralix.tan.BasicTest;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.RegionDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OtherPlayerRegionNameTest extends BasicTest  {

    @Override
    @BeforeEach
    protected void setUp() {
        super.setUp();
    }


    @Test
    void nominalTest() {

        Player player = server.addPlayer("player name");
        ITanPlayer tanPlayer  = PlayerDataStorage.getInstance().get(player);

        TownData townData = TownDataStorage.getInstance().newTown("Town", tanPlayer);
        RegionData regionData = RegionDataStorage.getInstance().createNewRegion("Region", townData);

        OtherPlayerRegionName entry = new OtherPlayerRegionName();

        String name = entry.getData(player, "player_{player name}_region_name");

        assertEquals(regionData.getName(), name);
    }

    @Test
    void noRegionTest() {

        Player player = server.addPlayer("player name");
        PlayerDataStorage.getInstance().get(player);

        OtherPlayerRegionName entry = new OtherPlayerRegionName();

        String name = entry.getData(player, "player_{player name}_region_name");

        assertEquals(Lang.NO_REGION.get(), name);
    }

}