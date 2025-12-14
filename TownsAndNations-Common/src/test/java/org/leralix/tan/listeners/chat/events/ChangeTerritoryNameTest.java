package org.leralix.tan.listeners.chat.events;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.leralix.lib.SphereLib;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class ChangeTerritoryNameTest {

    private static Player player;
    private static TownData townData;

    @BeforeEach
    void setUp() {
        ServerMock server = MockBukkit.mock();

        MockBukkit.load(SphereLib.class);
        MockBukkit.load(TownsAndNations.class);

        player = server.addPlayer();
        ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(player);
        townData = TownDataStorage.getInstance().newTown("town 1", tanPlayer);
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
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