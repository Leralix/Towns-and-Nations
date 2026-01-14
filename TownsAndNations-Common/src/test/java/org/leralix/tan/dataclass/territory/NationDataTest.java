package org.leralix.tan.dataclass.territory;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import org.leralix.tan.BasicTest;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.storage.stored.NationDataStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.RegionDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NationDataTest extends BasicTest {


    @Test
    void CreateNation(){

        String nationName = "FirstNation";

        Player bukkitPlayer = server.addPlayer();
        ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(bukkitPlayer);
        TownData townData = TownDataStorage.getInstance().newTown("FirstTown", tanPlayer);
        RegionData regionData = RegionDataStorage.getInstance().createNewRegion("FirstRegion", townData);
        NationData nationData = NationDataStorage.getInstance().createNewNation(nationName, regionData);


        assertEquals(nationName, nationData.getName());
        assertEquals(regionData, nationData.getCapital());
        assertEquals(townData.getLeaderData(), nationData.getLeaderData());
        assertEquals(1, nationData.getSubjects().size());


    }


}
