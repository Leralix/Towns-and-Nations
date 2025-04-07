package org.leralix.tan.listeners.chat.events;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.leralix.lib.position.Vector3D;
import org.leralix.tan.dataclass.PlayerData;
import org.leralix.tan.dataclass.PropertyData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.factory.AbstractionFactory;
import org.leralix.tan.storage.stored.TownDataStorage;

import static org.junit.jupiter.api.Assertions.*;

class ChangePropertyRentPriceTest {

    @BeforeAll
    static void setUp() {
        AbstractionFactory.initializeConfigs();
    }

    @Test
    void nominalCase() {

        TownData townData = TownDataStorage.getInstance().newTown("town 1");
        PlayerData playerData = AbstractionFactory.getRandomPlayerData();
        Player player = playerData.getPlayer();


        PropertyData propertyData = townData.registerNewProperty(
                new Vector3D(0,0,0,"world"),
                new Vector3D(3, 3, 3, "world"),
                playerData
                );

        ChangePropertyRentPrice changePropertyRentPrice = new ChangePropertyRentPrice(propertyData, null);

        changePropertyRentPrice.execute(player, "1000");

        assertEquals(1000, propertyData.getRentPrice());
    }

    @Test
    void wrongMessage() {
        TownData townData = TownDataStorage.getInstance().newTown("town 1");
        PlayerData playerData = AbstractionFactory.getRandomPlayerData();
        Player player = AbstractionFactory.getRandomPlayer();


        PropertyData propertyData = townData.registerNewProperty(
                new Vector3D(0,0,0,"world"),
                new Vector3D(3, 3, 3, "world"),
                playerData
        );

        ChangePropertyRentPrice changePropertyRentPrice = new ChangePropertyRentPrice(propertyData, null);

        changePropertyRentPrice.execute(player, "I000");

        assertEquals(0, propertyData.getRentPrice());
    }
}