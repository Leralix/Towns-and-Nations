package org.leralix.tan.listeners.chat.events;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.leralix.tan.BasicTest;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.TownData;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class ChangeTerritoryNameTest extends BasicTest {

    private static Player player;
    private static ITanPlayer tanPlayer;
    private static TownData townData;

    @Override
    @BeforeEach
    protected void setUp() {
        super.setUp();

        player = server.addPlayer();
        tanPlayer = playerDataStorage.get(player);
        townData = townDataStorage.newTown("town 1", tanPlayer);
    }

    @Test
    void nominalCase() {

        townData.addToBalance(50);
        String newName = "NewName";

        ChangeTerritoryName changeTerritoryName = new ChangeTerritoryName(townData, 25, null);
        changeTerritoryName.execute(player, tanPlayer, newName);

        assertEquals(newName, townData.getName());
        assertEquals(25, townData.getBalance());
    }

    @Test
    void notEnoughMoney() {

        String newName = "NewName";

        ChangeTerritoryName changeTerritoryName = new ChangeTerritoryName(townData, 1, null);
        changeTerritoryName.execute(player, tanPlayer, newName);

        assertNotEquals(newName, townData.getName());
    }
}