package org.leralix.tan.listeners.chat.events;

import org.junit.jupiter.api.Test;
import org.leralix.tan.BasicTest;
import org.leralix.tan.data.territory.TownData;
import org.leralix.tan.storage.stored.TownDataStorage;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CreateRankTest extends BasicTest {

    @Test
    void nominalCase() {
        var tanPlayer = townsAndNations.getPlayerDataStorage().get(server.addPlayer());
        TownData townData = TownDataStorage.getInstance().newTown("TestTown", tanPlayer);

        assertEquals(1, townData.getAllRanks().size());

        CreateRank createRank = new CreateRank(townData, null);
        createRank.execute(tanPlayer.getPlayer(), "TestRank");
        assertEquals(2, townData.getAllRanks().size());
    }

    @Test
    void duplicateNameAllowed() {
        var tanPlayer = townsAndNations.getPlayerDataStorage().get(server.addPlayer());
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