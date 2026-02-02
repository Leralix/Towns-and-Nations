package org.leralix.tan.listeners.chat;

import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.junit.jupiter.api.Test;
import org.leralix.tan.BasicTest;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.lang.Lang;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;

class ChatListenerTest extends BasicTest {


    @Test
    void playerTypedCancel(){

        Player player = server.addPlayer();
        ITanPlayer playerData = townsAndNations.getPlayerDataStorage().register(player);
        ChatListenerEvent mockedChatListenerEvent = mock(ChatListenerEvent.class);
        PlayerChatListenerStorage.register(player, playerData.getLang(), mockedChatListenerEvent);
        var asyncPlayerChatEvent = new AsyncPlayerChatEvent(false, player, Lang.CANCEL_WORD.get(langType), Set.of() );
        ChatListener chatListener = new ChatListener(townsAndNations.getPlayerDataStorage());

        chatListener.checkForCancelWord(asyncPlayerChatEvent);

        assertFalse(PlayerChatListenerStorage.contains(player));
    }


}