package org.leralix.tan.listeners.chat.events;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.factory.AbstractionFactory;
import org.leralix.tan.storage.stored.TownDataStorage;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CreateRankTest {

    @BeforeAll
    static void setUp() {
        AbstractionFactory.initializeConfigs();
    }

    @Test
    void nominalCase() {
        var tanPlayer = AbstractionFactory.getRandomITanPlayer();
        TownData townData = TownDataStorage.getInstance().newTown("TestTown", tanPlayer);

        assertEquals(1, townData.getAllRanks().size());

        CreateRank createRank = new CreateRank(townData, null);
        createRank.execute(tanPlayer.getPlayer(), "TestRank");
        assertEquals(2, townData.getAllRanks().size());
    }

    @Test
    void duplicateNameAllowed() {
        var tanPlayer = AbstractionFactory.getRandomITanPlayer();
        TownData townData = TownDataStorage.getInstance().newTown("TestTown", tanPlayer);
        String newRankName = "TestRank";

        assertEquals(1, townData.getAllRanks().size());
        CreateRank createRank = new CreateRank(townData, null);
        createRank.execute(tanPlayer.getPlayer(), newRankName);
        assertEquals(2, townData.getAllRanks().size());
        createRank.execute(tanPlayer.getPlayer(), newRankName);
        assertEquals(3, townData.getAllRanks().size());
    }

}