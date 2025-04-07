package org.leralix.tan.listeners.chat.events;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.leralix.tan.dataclass.PlayerData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.factory.AbstractionFactory;
import org.leralix.tan.storage.stored.TownDataStorage;

import static org.junit.jupiter.api.Assertions.*;
class ChangeTownTagTest {

    @BeforeAll
    static void setUp() {
        AbstractionFactory.initializeConfigs();
    }


    @Test
    void nominalCase() {
        PlayerData playerData = AbstractionFactory.getRandomPlayerData();
        Player player = playerData.getPlayer();
        TownData townData = TownDataStorage.getInstance().newTown("TestTown", playerData);

        String newTag = "TAG";

        ChangeTownTag changeTownTag = new ChangeTownTag(townData, null);
        changeTownTag.execute(player, newTag);

        assertEquals(newTag, townData.getTownTag());
    }

    @Test
    void wrongSizeCase() {
        PlayerData playerData = AbstractionFactory.getRandomPlayerData();
        Player player = playerData.getPlayer();
        TownData townData = TownDataStorage.getInstance().newTown("TestTown", playerData);

        String newTag = "goofy ahh tag";

        ChangeTownTag changeTownTag = new ChangeTownTag(townData, null);
        changeTownTag.execute(player, newTag);

        assertNotEquals(newTag, townData.getTownTag());
    }
}