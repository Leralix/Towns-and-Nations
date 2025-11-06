package org.leralix.tan.utils.gameplay;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.leralix.tan.BasicTest;
import org.leralix.tan.gui.service.requirements.model.AnyWoodScope;

class InventoryUtilTest extends BasicTest {

  @Override
  @BeforeEach
  protected void setUp() {
    super.setUp();
  }

  @Test
  public void testItemsNumberInInventory_has0() {
    int number = InventoryUtil.getItemsNumberInInventory(server.addPlayer(), new AnyWoodScope());

    assertEquals(0, number);
  }

  @Test
  public void testPlayerEnoughItem_notEnough() {
    assertFalse(InventoryUtil.playerEnoughItem(server.addPlayer(), new AnyWoodScope(), 50));
  }
}
