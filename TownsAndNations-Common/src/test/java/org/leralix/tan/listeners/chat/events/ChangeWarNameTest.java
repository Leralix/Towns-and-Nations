package org.leralix.tan.listeners.chat.events;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.leralix.lib.SphereLib;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.data.territory.TownData;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.war.War;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ChangeWarNameTest {

    private ServerMock server;

    @BeforeEach
    void setUp() {
        server = MockBukkit.mock();

        MockBukkit.load(SphereLib.class);
        MockBukkit.load(TownsAndNations.class);
    }

    @AfterEach
    public void tearDown()
    {
        MockBukkit.unmock();
    }

    @Test
    void nominalCase() {

        Player player = server.addPlayer();

        TownData town1 = TownDataStorage.getInstance().newTown("town 1");
        TownData town2 = TownDataStorage.getInstance().newTown("town 2");

        War war = new War("W1", town1, town2, Collections.emptyList());

        ChangeWarName changeWarName = new ChangeWarName(war, null);

        String newName = "new war name";
        changeWarName.execute(player, newName);

        assertEquals(newName, war.getName());

    }

}