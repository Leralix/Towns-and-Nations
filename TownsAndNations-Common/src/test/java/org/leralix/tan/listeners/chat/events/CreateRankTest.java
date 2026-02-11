package org.leralix.tan.listeners.chat.events;

import org.junit.jupiter.api.Test;
import org.leralix.tan.BasicTest;
import org.leralix.tan.data.territory.TownData;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CreateRankTest extends BasicTest {

    @Test
    void nominalCase() {
        var tanPlayer = playerDataStorage.get(server.addPlayer());
        TownData townData = townDataStorage.newTown("TestTown", tanPlayer);

        assertEquals(1, townData.getAllRanks().size());

        CreateRank createRank = new CreateRank(townData, null);
        createRank.execute(tanPlayer.getPlayer(), tanPlayer, "TestRank");
        assertEquals(2, townData.getAllRanks().size());
    }

    @Test
    void duplicateNameAllowed() {
        var tanPlayer = playerDataStorage.get(server.addPlayer());
        TownData townData = townDataStorage.newTown("TestTown", tanPlayer);
        String newRankName = "TestRank";

        assertEquals(1, townData.getAllRanks().size());
        CreateRank createRank = new CreateRank(townData, null);
        createRank.execute(tanPlayer.getPlayer(), tanPlayer, newRankName);
        assertEquals(2, townData.getAllRanks().size());
        createRank.execute(tanPlayer.getPlayer(), tanPlayer, newRankName);
        assertEquals(2, townData.getAllRanks().size());
    }

}