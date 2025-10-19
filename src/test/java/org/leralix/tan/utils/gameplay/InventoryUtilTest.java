package org.leralix.tan.utils.gameplay;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.leralix.tan.BasicTest;
import org.leralix.tan.gui.service.requirements.model.AllWoodScope;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class InventoryUtilTest extends BasicTest {

    @Override
    @BeforeEach
    protected void setUp() {
        super.setUp();
    }

    @Test
    public void testItemsNumberInInventory_has0() {

        int number = InventoryUtil.getItemsNumberInInventory(server.addPlayer(), new AllWoodScope());

        assertEquals(0, number);
    }

    @Test
    public void testPlayerEnoughItem_notEnough() {
        assertFalse(InventoryUtil.playerEnoughItem(server.addPlayer(), new AllWoodScope(), 50));
    }

}