package org.leralix.tan.listeners.chat.events;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.leralix.lib.SphereLib;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DonateToTerritoryTest {

    private ServerMock server;

    @BeforeEach
    void setUp() {
        server = MockBukkit.mock();

        MockBukkit.load(SphereLib.class);
        MockBukkit.load(TownsAndNations.class);
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void nominalCase() {
        var tanPlayer = PlayerDataStorage.getInstance().get(server.addPlayer());
        TownData townData = TownDataStorage.getInstance().newTown("townToDonate", tanPlayer);
        int amount = 1;
        tanPlayer.addToBalance(amount);

        DonateToTerritory donateToTerritory = new DonateToTerritory(townData);
        donateToTerritory.execute(tanPlayer.getPlayer(), String.valueOf(amount));

        assertEquals(amount, townData.getBalance());
    }

    @Test
    void notEnoughMoney() {
        var tanPlayer = PlayerDataStorage.getInstance().get(server.addPlayer());
        TownData townData = TownDataStorage.getInstance().newTown("townToDonate", tanPlayer);

        int amount = (int) (tanPlayer.getBalance() + 1);

        DonateToTerritory donateToTerritory = new DonateToTerritory(townData);
        donateToTerritory.execute(tanPlayer.getPlayer(), String.valueOf(amount));

        assertEquals(0, townData.getBalance());
    }

    @Test
    void notANumber() {
        var tanPlayer = PlayerDataStorage.getInstance().get(server.addPlayer());
        TownData townData = TownDataStorage.getInstance().newTown("townToDonate", tanPlayer);

        String amount = "notANumber";

        DonateToTerritory donateToTerritory = new DonateToTerritory(townData);
        donateToTerritory.execute(tanPlayer.getPlayer(), amount);

        assertEquals(0, townData.getBalance());
    }


}