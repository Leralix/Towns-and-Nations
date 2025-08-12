package org.leralix.tan.listeners.chat.events;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.factory.AbstractionFactory;
import org.leralix.tan.storage.stored.CurrentWarStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.war.PlannedAttack;
import org.leralix.tan.war.War;
import org.leralix.tan.war.legacy.CreateAttackData;
import org.leralix.tan.war.legacy.WarRole;

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

        War war = new War("W1", town1, town2);

        CreateAttackData createAttackData = new CreateAttackData(war, WarRole.MAIN_ATTACKER);
        PlannedAttack plannedAttack = CurrentWarStorage.newAttack(createAttackData);

        ChangeAttackName changeAttackName = new ChangeAttackName(plannedAttack, null);

        String newName = "new war name";
        changeAttackName.execute(player, newName);

        assertEquals(newName, plannedAttack.getName());

    }

}