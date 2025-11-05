package org.leralix.tan.listeners.chat.events;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.leralix.lib.position.Vector3D;
import org.leralix.tan.BasicTest;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.PropertyData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.gameplay.ItemStackSerializer;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;

class ChangePropertyRentPriceTest extends BasicTest {

    private Player player;
    private PropertyData propertyData;
    private MockedStatic<ItemStackSerializer> mockedSerializer;

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

        player = server.addPlayer();
        World world = server.addSimpleWorld("world");
        ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(player).join();
        TownData townData = TownDataStorage.getInstance().newTown("town 1").join();

        propertyData = townData.registerNewProperty(
                new Vector3D(new Location(world, 0, 0, 0)),
                new Vector3D(new Location(world, 3, 3, 3)),
                tanPlayer
        );
    }

    @Override
    @AfterEach
    protected void tearDown() {
        if (mockedSerializer != null) {
            mockedSerializer.close();
        }
        super.tearDown();
    }

    @Test
    void nominalCase() {

        ChangePropertyRentPrice changePropertyRentPrice = new ChangePropertyRentPrice(propertyData, null);

        changePropertyRentPrice.execute(player, "1000");

        assertEquals(1000, propertyData.getBaseRentPrice());
    }

    @Test
    void wrongMessage() {

        ChangePropertyRentPrice changePropertyRentPrice = new ChangePropertyRentPrice(propertyData, null);

        changePropertyRentPrice.execute(player, "1%");

        assertEquals(0, propertyData.getBaseRentPrice());
    }
}