package org.leralix.tan.commands.admin;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.leralix.tan.factory.AbstractionFactory;
import org.leralix.tan.storage.SudoPlayerStorage;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;


public class SudoPlayerTest {


    public static CommandSender sender;

    @BeforeAll
    static void initialise(){
        sender = Mockito.mock(CommandSender.class);
        AbstractionFactory.initializeConfigs();
    }

    @Test
    void standardUse() {
        Player player = AbstractionFactory.getRandomPlayer();

        SudoPlayer sudoPlayer = new SudoPlayer();
        sudoPlayer.perform(player, new String[]{"sudo"});

        assertTrue(SudoPlayerStorage.isSudoPlayer(player));
    }

    @Test
    void useOnItself() {
        Player player = AbstractionFactory.getRandomPlayer();

        SudoPlayer sudoPlayer = new SudoPlayer();
        sudoPlayer.perform(player, new String[]{"sudo", player.getName()});

        assertTrue(SudoPlayerStorage.isSudoPlayer(player));
    }

    @Test
    void OtherPlayerUser() {
        Player player = AbstractionFactory.getRandomPlayer();
        Player playerToGiveSudo = AbstractionFactory.getRandomPlayer();

        SudoPlayer sudoPlayer = new SudoPlayer();
        sudoPlayer.perform(player, new String[]{"sudo", playerToGiveSudo.getName()});

        assertFalse(SudoPlayerStorage.isSudoPlayer(player));
        assertTrue(SudoPlayerStorage.isSudoPlayer(playerToGiveSudo));
    }

}
