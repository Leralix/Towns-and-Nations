package org.leralix.tan.listeners.chat.events;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.dataclass.wars.CreateAttackData;
import org.leralix.tan.dataclass.wars.PlannedAttack;
import org.leralix.tan.dataclass.wars.wargoals.ConquerWarGoal;
import org.leralix.tan.factory.AbstractionFactory;
import org.leralix.tan.storage.stored.PlannedAttackStorage;
import org.leralix.tan.storage.stored.TownDataStorage;

import static org.junit.jupiter.api.Assertions.*;

class ChangeAttackNameTest {

    @BeforeAll
    static void setUp() {
        AbstractionFactory.initializeConfigs();
    }

    @Test
    void nominalCase() {

        Player player = AbstractionFactory.getRandomPlayer();

        TownData town1 = TownDataStorage.getInstance().newTown("town 1");
        TownData town2 = TownDataStorage.getInstance().newTown("town 2");

        CreateAttackData createAttackData = new CreateAttackData(town1, town2);
        createAttackData.setWarGoal(new ConquerWarGoal(town1, town2));
        PlannedAttack plannedAttack = PlannedAttackStorage.newWar(createAttackData);

        ChangeAttackName changeAttackName = new ChangeAttackName(plannedAttack, null);

        String newName = "new war name";
        changeAttackName.execute(player, newName);

        assertEquals(newName, plannedAttack.getName());

    }

}