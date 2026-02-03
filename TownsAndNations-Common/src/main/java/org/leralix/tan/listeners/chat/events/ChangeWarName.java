package org.leralix.tan.listeners.chat.events;

import org.bukkit.entity.Player;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.listeners.chat.ChatListenerEvent;
import org.leralix.tan.war.War;

import java.util.function.Consumer;

public class ChangeWarName extends ChatListenerEvent {
    private final War war;
    Consumer<Player> guiCallback;

    public ChangeWarName(War war, Consumer<Player> guiCallback) {
        this.war = war;
        this.guiCallback = guiCallback;
    }

    @Override
    public boolean execute(Player player, ITanPlayer playerData, String message) {
        war.setName(message);
        openGui(guiCallback, player);
        return true;
    }
}
