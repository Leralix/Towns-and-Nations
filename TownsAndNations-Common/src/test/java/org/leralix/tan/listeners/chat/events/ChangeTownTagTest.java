package org.leralix.tan.listeners.chat.events;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.leralix.tan.BasicTest;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.TownData;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.mockbukkit.mockbukkit.MockBukkit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class ChangeTownTagTest extends BasicTest {

    private static Player player;
    private static ITanPlayer tanPlayer;
    private static TownData townData;

    @Override
    @BeforeEach
    protected void setUp() {
        super.setUp();
        player = server.addPlayer();
        tanPlayer = townsAndNations.getPlayerDataStorage().get(player);
        townData = TownDataStorage.getInstance().newTown("town 1", tanPlayer);
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
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