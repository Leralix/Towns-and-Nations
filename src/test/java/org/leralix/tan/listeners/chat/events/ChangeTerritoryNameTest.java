package org.leralix.tan.listeners.chat.events;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.leralix.tan.dataclass.PlayerData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.factory.AbstractionFactory;
import org.leralix.tan.storage.stored.TownDataStorage;

import static org.junit.jupiter.api.Assertions.*;

class ChangeTerritoryNameTest {

    @BeforeAll
    static void setUp() {
        AbstractionFactory.initializeConfigs();
    }


    @Test
    void nominalCase() {

        Player player = AbstractionFactory.getRandomPlayer();
        TownData townData = TownDataStorage.getInstance().newTown("TestTown");
        String newName = "NewName";

        ChangeTerritoryName changeTerritoryName = new ChangeTerritoryName(townData, 0, null);
        changeTerritoryName.execute(player, newName);

        assertEquals(newName, townData.getName());

    }

}