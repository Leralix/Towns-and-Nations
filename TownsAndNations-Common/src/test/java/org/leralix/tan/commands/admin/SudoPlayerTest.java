package org.leralix.tan.commands.admin;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.leralix.lib.SphereLib;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.storage.SudoPlayerStorage;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class SudoPlayerTest {

    private ServerMock server;

    private PlayerMock playerMock;

    @BeforeEach
    void setUp() {
        server = MockBukkit.mock();

        MockBukkit.load(SphereLib.class);
        MockBukkit.load(TownsAndNations.class);


        playerMock = server.addPlayer("TestPlayer");
        playerMock.setOp(true);
    }

    @Test
    void standardUse() {

        assertFalse(SudoPlayerStorage.isSudoPlayer(playerMock));

        server.dispatchCommand(playerMock, "tanadmin sudo");

        assertTrue(SudoPlayerStorage.isSudoPlayer(playerMock));
    }

    @Test
    void OtherPlayerUser() {

        PlayerMock secondPlayerMock = server.addPlayer("SecondTestPlayer");

        server.dispatchCommand(playerMock, "tanadmin sudo " + secondPlayerMock.getName());


        assertFalse(SudoPlayerStorage.isSudoPlayer(playerMock));
        assertTrue(SudoPlayerStorage.isSudoPlayer(secondPlayerMock));
    }

    @AfterEach
    public void tearDown()
    {
        MockBukkit.unmock();
    }
}
