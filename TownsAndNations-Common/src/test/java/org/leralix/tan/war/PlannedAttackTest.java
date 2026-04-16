package org.leralix.tan.war;

import org.junit.jupiter.api.Test;
import org.leralix.tan.BasicTest;
import org.leralix.tan.data.territory.Town;
import org.leralix.tan.storage.CurrentAttacksStorage;
import org.leralix.tan.war.info.WarRole;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlannedAttackTest extends BasicTest {


    @Test
    public void test_correctTimestamp() {

        Town town1 = townStorage.newTown("town1");
        Town town2 = townStorage.newTown("town2");

        WarData war = new WarData("0", town1, town2);
        PlannedAttack plannedAttack = new PlannedAttack("0", war, WarRole.MAIN_ATTACKER, 0, 30);
        plannedAttack.startAttack();

        assertEquals(1, CurrentAttacksStorage.getAll().size());

    }
}