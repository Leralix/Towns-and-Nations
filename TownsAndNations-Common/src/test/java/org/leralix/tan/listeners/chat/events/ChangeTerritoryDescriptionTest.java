package org.leralix.tan.listeners.chat.events;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.leralix.tan.BasicTest;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.Region;
import org.leralix.tan.data.territory.Territory;
import org.leralix.tan.data.territory.Town;
import org.leralix.tan.utils.constants.Constants;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class ChangeTerritoryDescriptionTest extends BasicTest {

    private static Player player;
    private static ITanPlayer tanPlayer;
    private static Town townData;

    @Override
    @BeforeEach
    protected void setUp() {
        super.setUp();

        player = server.addPlayer();
        tanPlayer = playerDataStorage.get(player);
        townData = townStorage.newTown("town 1", tanPlayer);
    }


    @Test
    void nominalCaseTown() {

        ChangeTerritoryDescription changeDescription = new ChangeTerritoryDescription(townData, null);
        String description = "new description 1";

        changeDescription.execute(player, tanPlayer, description);

        assertEquals(description, townData.getDescription());
    }

    @Test
    void nominalCaseRegion() {

        Region regionData = regionStorage.newRegion("nation 1", townData);
        ChangeTerritoryDescription changeDescription = new ChangeTerritoryDescription(regionData, null);
        String description = "new description 2";

        changeDescription.execute(player, tanPlayer, description);

        assertEquals(description, regionData.getDescription());
    }

    @Test
    void nominalCaseTerritory() {

        Territory territoryData = townData;
        ChangeTerritoryDescription changeDescription = new ChangeTerritoryDescription(territoryData, null);
        String description = "new description 3";

        changeDescription.execute(player, tanPlayer, description);

        assertEquals(description, territoryData.getDescription());
    }

    @Test
    void DescTooBigError() {
        int maxDescriptionLength = Constants.getTownMaxDescriptionSize();

        StringBuilder description = new StringBuilder("a");
        description.append("a".repeat(maxDescriptionLength));

        ChangeTerritoryDescription changeDescription = new ChangeTerritoryDescription(townData, null);
        changeDescription.execute(player, tanPlayer, description.toString());

        assertNotEquals(description.toString(), townData.getDescription());
    }


}