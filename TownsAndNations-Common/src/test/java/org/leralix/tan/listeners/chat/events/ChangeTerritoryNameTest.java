package org.leralix.tan.listeners.chat.events;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.leralix.lib.SphereLib;
import org.leralix.tan.BasicTest;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.TownData;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class ChangeTerritoryNameTest extends BasicTest {

    private static Player player;
    private static TownData townData;

    @Override
    @BeforeEach
    protected void setUp() {
        ServerMock server = MockBukkit.mock();

        MockBukkit.load(SphereLib.class);
        MockBukkit.load(TownsAndNations.class);

        player = server.addPlayer();
        ITanPlayer tanPlayer = townsAndNations.getPlayerDataStorage().get(player);
        townData = TownDataStorage.getInstance().newTown("town 1", tanPlayer);
    }

    @Test
    void nominalCase() {

        townData.addToBalance(50);
        String newName = "NewName";

        ChangeTerritoryName changeTerritoryName = new ChangeTerritoryName(townData, 25, null);
        changeTerritoryName.execute(player, newName);

        assertEquals(newName, townData.getName());
        assertEquals(25, townData.getBalance());
    }

    @Test
    void notEnoughMoney() {

        String newName = "NewName";

        ChangeTerritoryName changeTerritoryName = new ChangeTerritoryName(townData, 1, null);
        changeTerritoryName.execute(player, newName);

        assertNotEquals(newName, townData.getName());
    }
}