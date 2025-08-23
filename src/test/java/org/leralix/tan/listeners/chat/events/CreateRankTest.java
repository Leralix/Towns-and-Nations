package org.leralix.tan.listeners.chat.events;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.leralix.lib.SphereLib;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CreateRankTest {

    private ServerMock server;

    @BeforeEach
    void setUp() {
        server = MockBukkit.mock();

        MockBukkit.load(SphereLib.class);
        MockBukkit.load(TownsAndNations.class);
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void nominalCase() {
        var tanPlayer = PlayerDataStorage.getInstance().get(server.addPlayer());
        TownData townData = TownDataStorage.getInstance().newTown("TestTown", tanPlayer);

        assertEquals(1, townData.getAllRanks().size());

        CreateRank createRank = new CreateRank(townData, null);
        createRank.execute(tanPlayer.getPlayer(), "TestRank");
        assertEquals(2, townData.getAllRanks().size());
    }

    @Test
    void duplicateNameAllowed() {
        var tanPlayer = PlayerDataStorage.getInstance().get(server.addPlayer());
        TownData townData = TownDataStorage.getInstance().newTown("TestTown", tanPlayer);
        String newRankName = "TestRank";

        assertEquals(1, townData.getAllRanks().size());
        CreateRank createRank = new CreateRank(townData, null);
        createRank.execute(tanPlayer.getPlayer(), newRankName);
        assertEquals(2, townData.getAllRanks().size());
        createRank.execute(tanPlayer.getPlayer(), newRankName);
        assertEquals(2, townData.getAllRanks().size());
    }

}