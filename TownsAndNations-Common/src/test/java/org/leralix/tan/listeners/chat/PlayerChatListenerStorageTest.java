package org.leralix.tan.listeners.chat;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import org.leralix.tan.BasicTest;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.lang.LangType;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PlayerChatListenerStorageTest extends BasicTest {


    @Test
    void registerPlayer(){

        Player player = server.addPlayer();
        PlayerChatListenerStorage.register(player, langType,null);

        assertTrue(PlayerChatListenerStorage.contains(player));
    }

    @Test
    void messageSuccess(){

        Player player = server.addPlayer();
        ITanPlayer tanPlayer = townsAndNations.getPlayerDataStorage().get(player);
        ChatListenerEvent mockedChatListenerEvent = mock(ChatListenerEvent.class);
        when(mockedChatListenerEvent.execute(any(Player.class), eq(tanPlayer), anyString())).thenReturn(true);
        PlayerChatListenerStorage.register(player, langType, mockedChatListenerEvent);

        PlayerChatListenerStorage.execute(player, tanPlayer, "");

        assertFalse(PlayerChatListenerStorage.contains(player));
    }

    /**
     * Assert that a failed chat message will keep the player registered to look to his next message.
     */
    @Test
    void messageError(){

        Player player = server.addPlayer();
        ITanPlayer tanPlayer = townsAndNations.getPlayerDataStorage().get(player);

        ChatListenerEvent mockedChatListenerEvent = mock(ChatListenerEvent.class);
        when(mockedChatListenerEvent.execute(any(Player.class), eq(tanPlayer), anyString())).thenReturn(false);
        PlayerChatListenerStorage.register(player, LangType.ENGLISH, mockedChatListenerEvent);

        PlayerChatListenerStorage.execute(player, tanPlayer, "");

        assertTrue(PlayerChatListenerStorage.contains(player));
    }
}