package org.leralix.tan.listeners.chat.events;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import org.leralix.tan.BasicTest;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.TownData;
import org.leralix.tan.storage.stored.TownDataStorage;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ChangeColorTest extends BasicTest {


    @Test
    void nominalCase() {

        Player player = server.addPlayer();
        ITanPlayer playerData = playerDataStorage.get(player);
        TownData townData = TownDataStorage.getInstance().newTown("town 1");

        ChangeColor changeColor = new ChangeColor(townData, null);

        changeColor.execute(player, playerData, "FF00FF");

        assertEquals(0xFF00FF, townData.getChunkColorCode());
    }

}