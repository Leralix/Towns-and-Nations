package org.leralix.tan.listeners.chatlistener.events;

import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.wars.PlannedAttack;
import org.leralix.tan.listeners.chatlistener.ChatListenerEvent;

import java.util.function.Consumer;

public class ChangeAttackName extends ChatListenerEvent {
    private PlannedAttack plannedAttack;
    Consumer<Player> guiCallback;
    public ChangeAttackName(PlannedAttack plannedAttack, Consumer<Player> guiCallback) {
        this.plannedAttack = plannedAttack;
        this.guiCallback = guiCallback;
    }

    @Override
    public void execute(Player player, String message) {
        plannedAttack.rename(message);
        openGui(guiCallback,player);
    }
}
