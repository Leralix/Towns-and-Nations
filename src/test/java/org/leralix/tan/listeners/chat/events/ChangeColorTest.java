package org.leralix.tan.listeners.chat.events;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.factory.AbstractionFactory;
import org.leralix.tan.storage.stored.TownDataStorage;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ChangeColorTest {


    @BeforeAll
    static void setUp() {
        AbstractionFactory.initializeConfigs();
    }

    @Test
    void nominalCase() {

        Player player = AbstractionFactory.getRandomPlayer();
        TownData townData = TownDataStorage.getInstance().newTown("town 1");

        ChangeColor changeColor = new ChangeColor(townData, null);

        changeColor.execute(player, "FF00FF");

        assertEquals(0xFF00FF, townData.getChunkColorCode());
    }

}