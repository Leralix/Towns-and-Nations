package org.leralix.tan.dataclass.territory;

import org.bukkit.Material;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.leralix.tan.dataclass.ITanPlayer;
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
        ITanPlayer tanPlayer = AbstractionFactory.getRandomITanPlayer();
        TownData townData = new TownData("T1", "testTown", tanPlayer);

        assertEquals("T1", townData.getID());
        assertEquals("testTown", townData.getName());
        assertEquals(tanPlayer, townData.getLeaderData());
        assertEquals(0, townData.getBalance());
        assertEquals(0, townData.getHierarchyRank());
        assertEquals(tanPlayer.getTownRankID(), townData.getDefaultRankID());
    }

    @Test
    void addRank(){
        ITanPlayer tanPlayer = AbstractionFactory.getRandomITanPlayer();
        TownData townData = TownDataStorage.getInstance().newTown("testTown", tanPlayer);

        assertEquals(townData.getTownDefaultRank(), tanPlayer.getTownRank());

        RankData newRank = townData.registerNewRank("Knight");
        newRank.incrementLevel();
        townData.setPlayerRank(tanPlayer, newRank);

        assertEquals(2, newRank.getLevel());
        assertEquals("Knight", newRank.getName());
        assertEquals(0, townData.getTownDefaultRank().getNumberOfPlayer());
        assertEquals(1, newRank.getNumberOfPlayer());
        assertEquals(2, townData.getRanks().size());
        assertEquals(newRank, tanPlayer.getTownRank());
    }

    @Test
    void deleteTownWithPlayers(){
        ITanPlayer tanPlayer = AbstractionFactory.getRandomITanPlayer();
        ITanPlayer othertanPlayer = AbstractionFactory.getRandomITanPlayer();
        TownData townData = TownDataStorage.getInstance().newTown("testTown", tanPlayer);

        assertEquals(1, townData.getPlayerIDList().size());
        assertEquals(townData.getID(), tanPlayer.getTownId());
        assertTrue(townData.getTownDefaultRank().getPlayersID().contains(tanPlayer.getID()));

        townData.addPlayer(othertanPlayer);

        assertEquals(2, townData.getPlayerIDList().size());
        assertEquals(townData.getID(), othertanPlayer.getTownId());
        assertTrue(townData.getTownDefaultRank().getPlayersID().contains(othertanPlayer.getID()));

        townData.delete();
        TownData otherTownData = TownDataStorage.getInstance().newTown("townToShowPlayerRank");

        assertNull(othertanPlayer.getTownId());
        assertNull(tanPlayer.getTownId());
        assertNull(tanPlayer.getRankID(otherTownData));
        assertNull(othertanPlayer.getRankID(otherTownData));
    }

    @Test
    void createGhostTown(){
        TownData townData = TownDataStorage.getInstance().newTown("ghost town");

        assertEquals(0, townData.getITanPlayerList().size());
        assertEquals(0, townData.getBalance());
        assertEquals(0, townData.getDefaultRank().getNumberOfPlayer());
        assertEquals(Material.SKELETON_SKULL, townData.getIcon().getType());
    }
}