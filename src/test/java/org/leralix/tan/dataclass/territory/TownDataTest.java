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
        ITanPlayer ITanPlayer = AbstractionFactory.getRandomITanPlayer();
        TownData townData = new TownData("T1", "testTown", ITanPlayer);

        assertEquals("T1", townData.getID());
        assertEquals("testTown", townData.getName());
        assertEquals(ITanPlayer, townData.getLeaderData());
        assertEquals(0, townData.getBalance());
        assertEquals(0, townData.getHierarchyRank());
        assertEquals(ITanPlayer.getTownRankID(), townData.getDefaultRankID());
    }

    @Test
    void addRank(){
        ITanPlayer ITanPlayer = AbstractionFactory.getRandomITanPlayer();
        TownData townData = TownDataStorage.getInstance().newTown("testTown", ITanPlayer);

        assertEquals(townData.getTownDefaultRank(), ITanPlayer.getTownRank());

        RankData newRank = townData.registerNewRank("Knight");
        newRank.incrementLevel();
        townData.setPlayerRank(ITanPlayer, newRank);

        assertEquals(2, newRank.getLevel());
        assertEquals("Knight", newRank.getName());
        assertEquals(0, townData.getTownDefaultRank().getNumberOfPlayer());
        assertEquals(1, newRank.getNumberOfPlayer());
        assertEquals(2, townData.getRanks().size());
        assertEquals(newRank, ITanPlayer.getTownRank());
    }

    @Test
    void deleteTownWithPlayers(){
        ITanPlayer ITanPlayer = AbstractionFactory.getRandomITanPlayer();
        ITanPlayer otherITanPlayer = AbstractionFactory.getRandomITanPlayer();
        TownData townData = TownDataStorage.getInstance().newTown("testTown", ITanPlayer);

        assertEquals(1, townData.getPlayerIDList().size());
        assertEquals(townData.getID(), ITanPlayer.getTownId());
        assertTrue(townData.getTownDefaultRank().getPlayersID().contains(ITanPlayer.getID()));

        townData.addPlayer(otherITanPlayer);

        assertEquals(2, townData.getPlayerIDList().size());
        assertEquals(townData.getID(), otherITanPlayer.getTownId());
        assertTrue(townData.getTownDefaultRank().getPlayersID().contains(otherITanPlayer.getID()));

        townData.delete();
        TownData otherTownData = TownDataStorage.getInstance().newTown("townToShowPlayerRank");

        assertNull(otherITanPlayer.getTownId());
        assertNull(ITanPlayer.getTownId());
        assertNull(ITanPlayer.getRankID(otherTownData));
        assertNull(otherITanPlayer.getRankID(otherTownData));
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