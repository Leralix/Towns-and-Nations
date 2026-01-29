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


class AddMoneyTest extends BasicTest {

    private PlayerMock playerMock;
    private ITanPlayer tanPlayer;

    @BeforeEach
    protected void setUp() {
        super.setUp();

        playerMock = server.addPlayer("TestPlayer");
        playerMock.setOp(true);

        tanPlayer = townsAndNations.getPlayerDataStorage().get(playerMock);
    }

    @Test
    void standardUse() {

        int amount = 100;

        server.dispatchCommand(playerMock, "tanadmin addmoney TestPlayer " + amount);

        assertEquals(Constants.getStartingBalance() + amount, tanPlayer.getBalance());
    }

    @Test
    void negativeValue() {

        int amount = -50;

        server.dispatchCommand(playerMock, "tanadmin addmoney TestPlayer " + amount);

        assertEquals(Constants.getStartingBalance() + amount, tanPlayer.getBalance());
    }


    @Test
    void wrongValue() {

        server.dispatchCommand(playerMock, "tanadmin addmoney TestPlayer 50€");

        assertEquals(Constants.getStartingBalance(), tanPlayer.getBalance());
    }

    @AfterEach
    public void tearDown()
    {
        MockBukkit.unmock();
    }
}
