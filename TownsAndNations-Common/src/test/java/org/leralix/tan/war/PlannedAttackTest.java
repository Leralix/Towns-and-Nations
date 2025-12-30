package org.leralix.tan.war;

import org.junit.jupiter.api.Test;
import org.leralix.tan.BasicTest;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.storage.CurrentAttacksStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.war.legacy.CreateAttackData;
import org.leralix.tan.war.legacy.WarRole;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlannedAttackTest extends BasicTest {


    @Test
    public void test_correctTimestamp() {

        TownData town1 = TownDataStorage.getInstance().newTown("town1");
        TownData town2 = TownDataStorage.getInstance().newTown("town2");

        War war = new War("0", town1, town2, Collections.emptyList());
        PlannedAttack plannedAttack = new PlannedAttack("0", war, WarRole.MAIN_ATTACKER, 0, 30);
        plannedAttack.startAttack();

        assertEquals(1, CurrentAttacksStorage.getAll().size());

    }
}