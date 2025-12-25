package org.leralix.tan.listeners.chat.events;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.leralix.lib.SphereLib;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.war.PlannedAttack;
import org.leralix.tan.war.War;
import org.leralix.tan.war.legacy.CreateAttackData;
import org.leralix.tan.war.legacy.WarRole;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ChangeAttackNameTest {

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

        CreateAttackData createAttackData = new CreateAttackData(war, WarRole.MAIN_ATTACKER);
        PlannedAttack plannedAttack = war.addAttack(createAttackData);

        ChangeAttackName changeAttackName = new ChangeAttackName(plannedAttack, null);

        String newName = "new war name";
        changeAttackName.execute(player, newName);

        assertEquals(newName, plannedAttack.getName());

    }

}