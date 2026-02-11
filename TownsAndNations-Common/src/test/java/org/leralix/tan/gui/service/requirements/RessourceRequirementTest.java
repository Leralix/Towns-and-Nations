package org.leralix.tan.gui.service.requirements;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.Test;
import org.leralix.tan.BasicTest;
import org.leralix.tan.data.territory.TerritoryData;
import org.leralix.tan.data.upgrade.Upgrade;
import org.leralix.tan.gui.service.requirements.model.AnyLogScope;
import org.leralix.tan.gui.service.requirements.model.MaterialScope;
import org.leralix.tan.gui.service.requirements.upgrade.ItemRequirementBuilder;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class RessourceRequirementTest extends BasicTest {

    @Test
    void testRessourceRequirement_invalid() {

        PlayerMock player = server.addPlayer();
        TerritoryData territoryData = townDataStorage.newTown("test");
        ItemRequirementBuilder itemRequirementBuilder =
                new ItemRequirementBuilder(
                        new MaterialScope(Material.COBBLESTONE),
                        List.of(10),
                        null);
        RessourceRequirement ressourceRequirement = new RessourceRequirement(
                itemRequirementBuilder,
                mock(Upgrade.class),
                territoryData,
                player
        );

        //Player does not have cobblestone
        assertTrue(ressourceRequirement.isInvalid());
    }

    @Test
    void testRessourceRequirement_valid() {

        PlayerMock player = server.addPlayer();
        int quantity = 10;
        TerritoryData territoryData = townDataStorage.newTown("test");

        player.getInventory().addItem(new ItemStack(Material.COBBLESTONE, quantity));
        ItemRequirementBuilder itemRequirementBuilder =
                new ItemRequirementBuilder(
                        new MaterialScope(Material.COBBLESTONE),
                        List.of(quantity),
                        null);
        RessourceRequirement ressourceRequirement = new RessourceRequirement(
                itemRequirementBuilder,
                mock(Upgrade.class),
                territoryData,
                player
        );

        assertFalse(ressourceRequirement.isInvalid());
    }

    @Test
    void testRessourceRequirement_customScope_valid() {

        PlayerMock player = server.addPlayer();
        TerritoryData territoryData = townDataStorage.newTown("test");
        int quantity = 10;

        player.getInventory().addItem(new ItemStack(Material.OAK_LOG, quantity));
        ItemRequirementBuilder itemRequirementBuilder =
                new ItemRequirementBuilder(
                        new AnyLogScope(),
                        List.of(quantity),
                        null);
        RessourceRequirement ressourceRequirement = new RessourceRequirement(
                itemRequirementBuilder,
                mock(Upgrade.class),
                territoryData,
                player
        );

        //Player does not have cobblestone
        assertFalse(ressourceRequirement.isInvalid());
    }

    @Test
    void testRessourceRequirement_customScope_isDone() {

        PlayerMock player = server.addPlayer();
        TerritoryData territoryData = townDataStorage.newTown("test");
        int quantity = 10;

        player.getInventory().addItem(new ItemStack(Material.OAK_LOG, quantity + 1));
        ItemRequirementBuilder itemRequirementBuilder =
                new ItemRequirementBuilder(
                        new AnyLogScope(),
                        List.of(quantity),
                        null);
        RessourceRequirement ressourceRequirement = new RessourceRequirement(
                itemRequirementBuilder,
                mock(Upgrade.class),
                territoryData,
                player
        );

        //Player does have enough oak
        ressourceRequirement.actionDone();
        assertNotNull(player.getInventory().getItem(0));
    }

    @Test
    void testRessourceRequirement_customScope_multipleStacks() {

        PlayerMock player = server.addPlayer();
        TerritoryData territoryData = townDataStorage.newTown("test");
        int quantity = 64;

        player.getInventory().addItem(new ItemStack(Material.OAK_LOG, quantity * 2));
        ItemRequirementBuilder itemRequirementBuilder =
                new ItemRequirementBuilder(
                        new AnyLogScope(),
                        List.of(quantity),
                        null);
        RessourceRequirement ressourceRequirement = new RessourceRequirement(
                itemRequirementBuilder,
                mock(Upgrade.class),
                territoryData,
                player
        );

        //Player does not have cobblestone
        ressourceRequirement.actionDone();
        assertNotNull(player.getInventory().getItem(1));
    }
}