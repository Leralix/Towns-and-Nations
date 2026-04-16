package org.leralix.tan.listeners.chat.events;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.leralix.tan.BasicTest;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.Town;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class ChangeTownTagTest extends BasicTest {

    private static Player player;
    private static ITanPlayer tanPlayer;
    private static Town townData;

    @Override
    @BeforeEach
    protected void setUp() {
        super.setUp();
        player = server.addPlayer();
        tanPlayer = playerDataStorage.get(player);
        townData = townStorage.newTown("town 1", tanPlayer);
    }

    @Test
    void nominalCase() {

        String newTag = "TAG";

        ChangeTownTag changeTownTag = new ChangeTownTag(townData, null);
        changeTownTag.execute(player, tanPlayer, newTag);

        assertEquals(newTag, townData.getTownTag());
    }

    @Test
    void wrongSizeCase() {

        String newTag = "goofy ahh tag";

        ChangeTownTag changeTownTag = new ChangeTownTag(townData, null);
        changeTownTag.execute(player, tanPlayer, newTag);

        assertNotEquals(newTag, townData.getTownTag());
    }
}