package org.leralix.tan.listeners.chat;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;

import java.util.Set;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.junit.jupiter.api.Test;
import org.leralix.tan.BasicTest;
import org.leralix.tan.lang.Lang;

class ChatListenerTest extends BasicTest {

  @Test
  void playerTypedCancel() {

    Player player = server.addPlayer();
    ChatListenerEvent mockedChatListenerEvent = mock(ChatListenerEvent.class);
    PlayerChatListenerStorage.register(player, mockedChatListenerEvent);
    var asyncPlayerChatEvent =
        new AsyncPlayerChatEvent(false, player, Lang.CANCEL_WORD.get(player), Set.of());
    ChatListener chatListener = new ChatListener();

    chatListener.checkForCancelWord(asyncPlayerChatEvent);

    assertFalse(PlayerChatListenerStorage.contains(player));
  }
}
