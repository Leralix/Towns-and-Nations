package org.leralix.tan.dataclass.territory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.leralix.tan.dataclass.PlayerData;
import org.leralix.tan.dataclass.RankData;
import org.leralix.tan.factory.AbstractionFactory;
import org.leralix.tan.storage.stored.TownDataStorage;

import static org.junit.jupiter.api.Assertions.*;

class TownDataTest {

    @BeforeAll
    static void setUp() {
        AbstractionFactory.initializeConfigs();
    }


    @Test
    void createTown(){
        PlayerData playerData = AbstractionFactory.getRandomPlayerData();
        TownData townData = new TownData("T1", "testTown", playerData);

        assertEquals("T1", townData.getID());
        assertEquals("testTown", townData.getName());
        assertEquals(playerData, townData.getLeaderData());
        assertEquals(0, townData.getBalance());
        assertEquals(0, townData.getHierarchyRank());
        assertEquals(playerData.getTownRankID(), townData.getDefaultRankID());
    }

    @Test
    void addRank(){
        PlayerData playerData = AbstractionFactory.getRandomPlayerData();
        TownData townData = TownDataStorage.getInstance().newTown("testTown", playerData);

        assertEquals(townData.getTownDefaultRank(), playerData.getTownRank());

        RankData newRank = townData.registerNewRank("Knight");
        newRank.incrementLevel();
        townData.setPlayerRank(playerData, newRank);

        assertEquals(2, newRank.getLevel());
        assertEquals("Knight", newRank.getName());
        assertEquals(0, townData.getTownDefaultRank().getNumberOfPlayer());
        assertEquals(1, newRank.getNumberOfPlayer());
        assertEquals(2, townData.getRanks().size());
        assertEquals(newRank, playerData.getTownRank());
    }

    @Test
    void deleteTownWithPlayers(){
        PlayerData playerData = AbstractionFactory.getRandomPlayerData();
        PlayerData otherPlayerData = AbstractionFactory.getRandomPlayerData();
        TownData townData = TownDataStorage.getInstance().newTown("testTown", playerData);

        assertEquals(1, townData.getPlayerIDList().size());
        assertEquals(townData.getID(), playerData.getTownId());
        assertTrue(townData.getTownDefaultRank().getPlayersID().contains(playerData.getID()));

        townData.addPlayer(otherPlayerData);

        assertEquals(2, townData.getPlayerIDList().size());
        assertEquals(townData.getID(), otherPlayerData.getTownId());
        assertTrue(townData.getTownDefaultRank().getPlayersID().contains(otherPlayerData.getID()));

        townData.delete();
        TownData otherTownData = TownDataStorage.getInstance().newTown("townToShowPlayerRank");

        assertNull(otherPlayerData.getTownId());
        assertNull(playerData.getTownId());
        assertNull(playerData.getRankID(otherTownData));
        assertNull(otherPlayerData.getRankID(otherTownData));
    }
}