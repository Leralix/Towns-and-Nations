package org.leralix.tan.listeners.chat.events;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import org.leralix.tan.BasicTest;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.storage.stored.TownDataStorage;

import static org.junit.jupiter.api.Assertions.assertTrue;

class CreateEmptyTownTest extends BasicTest {



    @Test
    void nominalCase() {

        Player player = server.addPlayer();
        ITanPlayer tanPlayer = townsAndNations.getPlayerDataStorage().get(player);

        String townName = "TestTown";
        CreateEmptyTown createEmptyTown = new CreateEmptyTown(null);
        createEmptyTown.execute(player, tanPlayer, townName);

        assertTrue(TownDataStorage.getInstance().isNameUsed(townName));
    }

}