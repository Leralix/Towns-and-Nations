package org.leralix.tan.listeners.chat.events;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.leralix.tan.BasicTest;
import org.leralix.tan.data.building.landmark.Landmark;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.storage.stored.LandmarkStorage;
import org.mockbukkit.mockbukkit.MockBukkit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class ChangeLandmarkNameTest extends BasicTest {

    private Landmark landmark;

    @Override
    @BeforeEach
    protected void setUp() {
        super.setUp();

        World world = server.addSimpleWorld("world");
        landmark = LandmarkStorage.getInstance().addLandmark(new Location(world, 0, 0, 0));
    }

    @AfterEach
    public void tearDown()
    {
        MockBukkit.unmock();
    }

    @Test
    void nominalCase() {

        Player player = server.addPlayer();
        ITanPlayer playerData = playerDataStorage.get(player);
        ChangeLandmarkName changeLandmarkName = new ChangeLandmarkName(landmark,25, null);

        String newName = "new landmark name";
        changeLandmarkName.execute(player, playerData, newName);

        assertEquals(newName, landmark.getName());
    }

    @Test
    void nameTooLong() {

        Player player = server.addPlayer();
        ITanPlayer playerData = playerDataStorage.get(player);
        int nameMaxSize = 5;

        ChangeLandmarkName changeLandmarkName = new ChangeLandmarkName(landmark, nameMaxSize,null);

        StringBuilder newName = new StringBuilder("a");
        newName.append("a".repeat(nameMaxSize));

        changeLandmarkName.execute(player, playerData, newName.toString());
        assertNotEquals(newName.toString(), landmark.getName());
    }

}