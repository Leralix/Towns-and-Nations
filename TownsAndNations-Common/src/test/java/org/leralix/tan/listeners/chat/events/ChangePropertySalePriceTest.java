package org.leralix.tan.listeners.chat.events;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.leralix.lib.SphereLib;
import org.leralix.lib.position.Vector3D;
import org.leralix.tan.BasicTest;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.data.building.property.PropertyData;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.TownData;
import org.leralix.tan.utils.gameplay.ItemStackSerializer;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;

class ChangePropertySalePriceTest extends BasicTest {

    private Player player;
    private ITanPlayer tanPlayer;
    private PropertyData propertyData;
    private MockedStatic<ItemStackSerializer> mockedSerializer;
    private TownsAndNations townsAndNations;

    @Override
    @BeforeEach
    protected void setUp() {
        super.setUp();

        mockedSerializer = mockStatic(ItemStackSerializer.class);
        mockedSerializer.when(() -> ItemStackSerializer.serializeItemStack(any(ItemStack.class)))
                .thenAnswer(invocation -> {
                    ItemStack arg = invocation.getArgument(0);
                    return arg.getType().name();
                });

        mockedSerializer.when(() -> ItemStackSerializer.deserializeItemStack(anyString()))
                .thenAnswer(invocation -> {
                    String arg = invocation.getArgument(0);
                    return new ItemStack(Material.valueOf(arg));
                });

        MockBukkit.load(SphereLib.class);
        townsAndNations = MockBukkit.load(TownsAndNations.class);


        player = server.addPlayer();
        World world = server.addSimpleWorld("world");
        tanPlayer = playerDataStorage.get(player);
        TownData townData = townDataStorage.newTown("town 1");

        propertyData = townData.registerNewProperty(
                new Vector3D(new Location(world, 0, 0, 0)),
                new Vector3D(new Location(world, 3, 3, 3)),
                tanPlayer
        );
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
        mockedSerializer.close();
        townsAndNations.resetSingletonForTests();
    }

    @Test
    void nominalCase() {

        ChangePropertySalePrice changePropertySalePrice = new ChangePropertySalePrice(propertyData, null);

        changePropertySalePrice.execute(player, tanPlayer, "1000");

        assertEquals(1000, propertyData.getPrice());
    }

    @Test
    void wrongMessage() {

        ChangePropertySalePrice changePropertySalePrice = new ChangePropertySalePrice(propertyData, null);

        changePropertySalePrice.execute(player, tanPlayer, "1%");

        assertEquals(0, propertyData.getRentPrice());
    }

}