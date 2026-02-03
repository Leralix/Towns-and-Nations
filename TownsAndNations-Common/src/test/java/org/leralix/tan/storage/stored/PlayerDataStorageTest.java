package org.leralix.tan.storage.stored;

import org.junit.jupiter.api.Test;
import org.leralix.tan.BasicTest;
import org.leralix.tan.data.player.ITanPlayer;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class PlayerDataStorageTest extends BasicTest {

    @Test
    void testCreatePlayerData() {
        PlayerDataStorage storage = townsAndNations.getPlayerDataStorage();

        PlayerMock player = server.addPlayer("TestPlayer");

        // Create player data
        ITanPlayer tanPlayer = storage.get(player);

        // Verify that the player data was created
        assertNotNull(tanPlayer, "Player data should be created and retrievable");
    }

}