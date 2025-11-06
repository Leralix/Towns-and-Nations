package org.leralix.tan.gui.service.requirements;

import static org.junit.jupiter.api.Assertions.*;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.Test;
import org.leralix.tan.BasicTest;
import org.leralix.tan.gui.service.requirements.model.AnyLogScope;
import org.leralix.tan.gui.service.requirements.model.MaterialScope;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

class RessourceRequirementTest extends BasicTest {

  @Test
  void testRessourceRequirement_invalid() {

    PlayerMock player = server.addPlayer();

    RessourceRequirement ressourceRequirement =
        new RessourceRequirement(new MaterialScope(Material.COBBLESTONE), 10, player);

    // Player does not have cobblestone
    assertTrue(ressourceRequirement.isInvalid());
  }

  @Test
  void testRessourceRequirement_valid() {

    PlayerMock player = server.addPlayer();
    int quantity = 10;

    player.getInventory().addItem(new ItemStack(Material.COBBLESTONE, quantity));
    RessourceRequirement ressourceRequirement =
        new RessourceRequirement(new MaterialScope(Material.COBBLESTONE), quantity, player);

    assertFalse(ressourceRequirement.isInvalid());
  }

  @Test
  void testRessourceRequirement_customScope_valid() {

    PlayerMock player = server.addPlayer();
    int quantity = 10;

    player.getInventory().addItem(new ItemStack(Material.OAK_LOG, quantity));
    RessourceRequirement ressourceRequirement =
        new RessourceRequirement(new AnyLogScope(), quantity, player);

    // Player does not have cobblestone
    assertFalse(ressourceRequirement.isInvalid());
  }

  @Test
  void testRessourceRequirement_customScope_isDone() {

    PlayerMock player = server.addPlayer();
    int quantity = 10;

    player.getInventory().addItem(new ItemStack(Material.OAK_LOG, quantity + 1));
    RessourceRequirement ressourceRequirement =
        new RessourceRequirement(new AnyLogScope(), quantity, player);

    // Player does not have cobblestone
    ressourceRequirement.actionDone();
    assertNotNull(player.getInventory().getItem(0));
  }

  @Test
  void testRessourceRequirement_customScope_multipleStacks() {

    PlayerMock player = server.addPlayer();
    int quantity = 64;

    player.getInventory().addItem(new ItemStack(Material.OAK_LOG, quantity * 2));
    RessourceRequirement ressourceRequirement =
        new RessourceRequirement(new AnyLogScope(), quantity, player);

    // Player does not have cobblestone
    ressourceRequirement.actionDone();
    assertNotNull(player.getInventory().getItem(1));
  }
}
