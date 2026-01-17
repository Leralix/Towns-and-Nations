package org.leralix.tan.commands.player;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import org.leralix.tan.BasicTest;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.NationData;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.storage.stored.NationDataStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.RegionDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TerritoryCommandUtilTest extends BasicTest {

    @Test
    void resolveTerritory_shouldBeCaseInsensitive_forTownRegionNation() {
        Player bukkitPlayer = server.addPlayer();
        ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(bukkitPlayer);

        TownData town = TownDataStorage.getInstance().newTown("TestTown", tanPlayer);
        RegionData region = RegionDataStorage.getInstance().createNewRegion("TestRegion", town);
        NationData nation = NationDataStorage.getInstance().createNewNation("TestNation", region);

        assertNotNull(town);
        assertNotNull(region);
        assertNotNull(nation);

        TerritoryData resolvedTown = TerritoryCommandUtil.resolveTerritory(bukkitPlayer, tanPlayer, "TOWN", "/tan claimarea <town/region/nation>");
        TerritoryData resolvedRegion = TerritoryCommandUtil.resolveTerritory(bukkitPlayer, tanPlayer, "ReGiOn", "/tan claimarea <town/region/nation>");
        TerritoryData resolvedNation = TerritoryCommandUtil.resolveTerritory(bukkitPlayer, tanPlayer, "nation", "/tan claimarea <town/region/nation>");

        assertEquals(town, resolvedTown);
        assertEquals(region, resolvedRegion);
        assertEquals(nation, resolvedNation);
    }
}
