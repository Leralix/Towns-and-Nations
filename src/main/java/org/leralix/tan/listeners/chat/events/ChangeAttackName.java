package org.leralix.tan.listeners.chat.events;

import org.bukkit.entity.Player;
import org.leralix.tan.listeners.chat.ChatListenerEvent;
import org.leralix.tan.war.PlannedAttack;

import java.util.function.Consumer;

public class ChangeAttackName extends ChatListenerEvent {
    private final PlannedAttack plannedAttack;
    Consumer<Player> guiCallback;
    public ChangeAttackName(PlannedAttack plannedAttack, Consumer<Player> guiCallback) {
        this.plannedAttack = plannedAttack;
        this.guiCallback = guiCallback;
    }

    @Override
    public boolean execute(Player player, String message) {
        plannedAttack.rename(message);
        openGui(guiCallback,player);
        return true;
    }
}
