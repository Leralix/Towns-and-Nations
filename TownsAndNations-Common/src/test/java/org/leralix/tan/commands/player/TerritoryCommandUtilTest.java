package org.leralix.tan.commands.player;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import org.leralix.tan.BasicTest;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.KingdomData;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.storage.stored.KingdomDataStorage;
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
        KingdomData kingdom = KingdomDataStorage.getInstance().createNewKingdom("TestKingdom", region);

        assertNotNull(town);
        assertNotNull(region);
        assertNotNull(kingdom);

        TerritoryData resolvedTown = TerritoryCommandUtil.resolveTerritory(bukkitPlayer, tanPlayer, "TOWN", "/tan claimarea <town/region/nation|kingdom>");
        TerritoryData resolvedRegion = TerritoryCommandUtil.resolveTerritory(bukkitPlayer, tanPlayer, "ReGiOn", "/tan claimarea <town/region/nation|kingdom>");
        TerritoryData resolvedNationAlias = TerritoryCommandUtil.resolveTerritory(bukkitPlayer, tanPlayer, "nation", "/tan claimarea <town/region/nation|kingdom>");
        TerritoryData resolvedKingdom = TerritoryCommandUtil.resolveTerritory(bukkitPlayer, tanPlayer, "KiNgDoM", "/tan claimarea <town/region/nation|kingdom>");

        assertEquals(town, resolvedTown);
        assertEquals(region, resolvedRegion);
        assertEquals(kingdom, resolvedNationAlias);
        assertEquals(kingdom, resolvedKingdom);
    }
}
