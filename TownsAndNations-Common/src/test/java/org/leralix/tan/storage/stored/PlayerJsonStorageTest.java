package org.leralix.tan.storage.stored;

import org.junit.jupiter.api.Test;
import org.leralix.tan.BasicTest;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.storage.stored.json.PlayerJsonStorage;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class PlayerJsonStorageTest extends BasicTest {

    @Test
    void testCreatePlayerData() {
        PlayerJsonStorage storage = playerDataStorage;

        PlayerMock player = server.addPlayer("TestPlayer");

        // Create player data
        ITanPlayer tanPlayer = storage.get(player);

        // Verify that the player data was created
        assertNotNull(tanPlayer, "Player data should be created and retrievable");
    }

}