package org.leralix.tan.data.territory;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import org.leralix.tan.BasicTest;
import org.leralix.tan.data.player.ITanPlayer;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NationDataTest extends BasicTest {


    @Test
    void CreateNation(){

        String nationName = "FirstNation";

        Player bukkitPlayer = server.addPlayer();
        ITanPlayer tanPlayer = playerDataStorage.get(bukkitPlayer);
        Town townData = townStorage.newTown("FirstTown", tanPlayer);
        Region regionData = regionStorage.newRegion("FirstRegion", townData);
        Nation nationData = nationStorage.newNation(nationName, regionData);


        assertEquals(nationName, nationData.getName());
        assertEquals(regionData, nationData.getCapital());
        assertEquals(townData.getLeaderData(), nationData.getLeaderData());
        assertEquals(1, nationData.getSubjects().size());


    }


}
