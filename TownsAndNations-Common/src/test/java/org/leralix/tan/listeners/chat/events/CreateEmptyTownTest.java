package org.leralix.tan.listeners.chat.events;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import org.leralix.tan.BasicTest;
import org.leralix.tan.data.player.ITanPlayer;

import static org.junit.jupiter.api.Assertions.assertTrue;

class CreateEmptyTownTest extends BasicTest {



    @Test
    void nominalCase() {

        Player player = server.addPlayer();
        ITanPlayer tanPlayer = playerDataStorage.get(player);

        String townName = "TestTown";
        CreateEmptyTown createEmptyTown = new CreateEmptyTown(null);
        createEmptyTown.execute(player, tanPlayer, townName);

        assertTrue(townDataStorage.isNameUsed(townName));
    }

}