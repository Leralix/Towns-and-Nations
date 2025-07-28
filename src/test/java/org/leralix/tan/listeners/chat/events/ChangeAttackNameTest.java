package org.leralix.tan.listeners.chat.events;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.factory.AbstractionFactory;
import org.leralix.tan.storage.stored.CurrentWarStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.war.CurrentWar;
import org.leralix.tan.war.legacy.CreateAttackData;
import org.leralix.tan.war.legacy.wargoals.ConquerWarGoal;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        CurrentWar plannedAttack = CurrentWarStorage.newWar(createAttackData);

        ChangeAttackName changeAttackName = new ChangeAttackName(plannedAttack, null);

        String newName = "new war name";
        changeAttackName.execute(player, newName);

        assertEquals(newName, plannedAttack.getName());

    }

}