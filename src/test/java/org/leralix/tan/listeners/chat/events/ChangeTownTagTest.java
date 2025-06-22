package org.leralix.tan.listeners.chat.events;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.factory.AbstractionFactory;
import org.leralix.tan.storage.stored.TownDataStorage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
class ChangeTownTagTest {

    @BeforeAll
    static void setUp() {
        AbstractionFactory.initializeConfigs();
    }


    @Test
    void nominalCase() {
        ITanPlayer tanPlayer = AbstractionFactory.getRandomITanPlayer();
        Player player = tanPlayer.getPlayer();
        TownData townData = TownDataStorage.getInstance().newTown("TestTown", tanPlayer);

        String newTag = "TAG";

        ChangeTownTag changeTownTag = new ChangeTownTag(townData, null);
        changeTownTag.execute(player, newTag);

        assertEquals(newTag, townData.getTownTag());
    }

    @Test
    void wrongSizeCase() {
        ITanPlayer tanPlayer = AbstractionFactory.getRandomITanPlayer();
        Player player = tanPlayer.getPlayer();
        TownData townData = TownDataStorage.getInstance().newTown("TestTown", tanPlayer);

        String newTag = "goofy ahh tag";

        ChangeTownTag changeTownTag = new ChangeTownTag(townData, null);
        changeTownTag.execute(player, newTag);

        assertNotEquals(newTag, townData.getTownTag());
    }
}