package org.leralix.tan.listeners.chat.events;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.factory.AbstractionFactory;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.RegionDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class ChangeTerritoryDescriptionTest {

    private static Player player;
    private static TownData townData;

    @BeforeAll
    static void setUp() {
        AbstractionFactory.initializeConfigs();
        player = AbstractionFactory.getRandomPlayer();
        ITanPlayer tanPlayer = PlayerDataStorage.getInstance().register(player);
        townData = TownDataStorage.getInstance().newTown("town 1", tanPlayer);
    }

    @Test
    void nominalCaseTown() {

        ChangeTerritoryDescription changeDescription = new ChangeTerritoryDescription(townData, null);
        String description = "new description 1";

        changeDescription.execute(player, description);

        assertEquals(description, townData.getDescription());
    }

    @Test
    void nominalCaseRegion() {

        RegionData regionData = RegionDataStorage.getInstance().createNewRegion("nation 1", townData);
        ChangeTerritoryDescription changeDescription = new ChangeTerritoryDescription(regionData, null);
        String description = "new description 2";

        changeDescription.execute(player, description);

        assertEquals(description, regionData.getDescription());
    }

    @Test
    void nominalCaseTerritory() {

        TerritoryData territoryData = townData;
        ChangeTerritoryDescription changeDescription = new ChangeTerritoryDescription(territoryData, null);
        String description = "new description 3";

        changeDescription.execute(player, description);

        assertEquals(description, territoryData.getDescription());
    }

    @Test
    void  DescTooBigError() {
        int maxDescriptionLength = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("TownDescSize");

        StringBuilder description = new StringBuilder("a");
        description.append("a".repeat(maxDescriptionLength));

        ChangeTerritoryDescription changeDescription = new ChangeTerritoryDescription(townData, null);
        changeDescription.execute(player, description.toString());

        assertNotEquals(description.toString(), townData.getDescription());
    }

    void DescTooShortError() {

        String  description = "a";
        ChangeTerritoryDescription changeDescription = new ChangeTerritoryDescription(townData, null);
        changeDescription.execute(player, description);

        assertNotEquals(description, townData.getDescription());
    }



}