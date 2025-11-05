package org.leralix.tan.commands.admin;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.leralix.lib.SphereLib;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.constants.Constants;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class SetMoneyTest {

    private ServerMock server;

    private PlayerMock playerMock;
    private ITanPlayer tanPlayer;

    @BeforeEach
    void setUp() {
        server = MockBukkit.mock();

        MockBukkit.load(SphereLib.class);
        MockBukkit.load(TownsAndNations.class);

        playerMock = server.addPlayer("TestPlayer");
        playerMock.setOp(true);

        tanPlayer = PlayerDataStorage.getInstance().get(playerMock).join();
    }

    @Test
    void standardUse() {
        server.dispatchCommand(playerMock, "tanadmin setmoney TestPlayer 10");

        assertEquals(10, tanPlayer.getBalance());
    }

    @Test
    void negativeValue() {
        server.dispatchCommand(playerMock, "tanadmin setmoney TestPlayer -500");

        assertEquals(-500, tanPlayer.getBalance());
    }


    @Test
    void wrongValue() {

        server.dispatchCommand(playerMock, "tanadmin setmoney TestPlayer 50€");

        assertEquals(Constants.getStartingBalance(), tanPlayer.getBalance());
    }

    @AfterEach
    public void tearDown()
    {
        MockBukkit.unmock();
    }
}
