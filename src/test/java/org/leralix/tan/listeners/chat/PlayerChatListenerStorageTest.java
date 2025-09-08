package org.leralix.tan.listeners.chat;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import org.leralix.tan.BasicTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PlayerChatListenerStorageTest extends BasicTest {


    @Test
    void registerPlayer(){

        Player player = server.addPlayer();
        PlayerChatListenerStorage.register(player, null);

        assertTrue(PlayerChatListenerStorage.contains(player));
    }

    @Test
    void messageSuccess(){

        Player player = server.addPlayer();
        ChatListenerEvent mockedChatListenerEvent = mock(ChatListenerEvent.class);
        when(mockedChatListenerEvent.execute(any(Player.class), anyString())).thenReturn(true);
        PlayerChatListenerStorage.register(player, mockedChatListenerEvent);

        PlayerChatListenerStorage.execute(player, "");

        assertFalse(PlayerChatListenerStorage.contains(player));
    }

    @Test
    void messageError(){

        Player player = server.addPlayer();
        ChatListenerEvent mockedChatListenerEvent = mock(ChatListenerEvent.class);
        when(mockedChatListenerEvent.execute(any(Player.class), anyString())).thenReturn(false);
        PlayerChatListenerStorage.register(player, mockedChatListenerEvent);

        PlayerChatListenerStorage.execute(player, "");

        assertFalse(PlayerChatListenerStorage.contains(player));
    }
}