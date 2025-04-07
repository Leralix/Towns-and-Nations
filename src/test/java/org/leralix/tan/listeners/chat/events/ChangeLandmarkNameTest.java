package org.leralix.tan.listeners.chat.events;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.dataclass.Landmark;
import org.leralix.tan.factory.AbstractionFactory;
import org.leralix.tan.storage.stored.LandmarkStorage;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ChangeLandmarkNameTest {

    @BeforeAll
    static void setUp() {
        AbstractionFactory.initializeConfigs();
    }

    @Test
    void nominalCase() {

        Player player = AbstractionFactory.getRandomPlayer();

        World world = AbstractionFactory.createWorld("world1", UUID.randomUUID());
        AbstractionFactory.createChunk(0, 0, world);
        Location location = AbstractionFactory.createLocation(0,0,0,world);

        Landmark landmark = LandmarkStorage.getInstance().addLandmark(location);

        ChangeLandmarkName changeLandmarkName = new ChangeLandmarkName(landmark, null);
        String newName = "new landmark name";

        changeLandmarkName.execute(player, newName);

        assertEquals(newName, landmark.getName());
    }

    @Test
    void nameTooLong() {

        Player player = AbstractionFactory.getRandomPlayer();

        World world = AbstractionFactory.createWorld("world1", UUID.randomUUID());
        AbstractionFactory.createChunk(0, 0, world);
        Location location = AbstractionFactory.createLocation(0,0,0,world);

        Landmark landmark = LandmarkStorage.getInstance().addLandmark(location);

        ChangeLandmarkName changeLandmarkName = new ChangeLandmarkName(landmark, null);

        int nameMaxSize = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("landmarkNameMaxSize");

        StringBuilder newName = new StringBuilder("a");
        newName.append("a".repeat(nameMaxSize));

        changeLandmarkName.execute(player, newName.toString());
        assertNotEquals(newName.toString(), landmark.getName());
    }

}