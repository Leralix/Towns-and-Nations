package org.leralix.tan.commands.admin;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.leralix.tan.BasicTest;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.utils.constants.Constants;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;


class SetMoneyTest extends BasicTest {

    private PlayerMock playerMock;
    private ITanPlayer tanPlayer;

    @Override
    @BeforeEach
    protected void setUp() {
        super.setUp();

        playerMock = server.addPlayer("TestPlayer");
        playerMock.setOp(true);

        tanPlayer = playerDataStorage.get(playerMock);
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
