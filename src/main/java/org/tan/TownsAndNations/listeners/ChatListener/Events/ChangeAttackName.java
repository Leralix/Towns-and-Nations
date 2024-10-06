package org.tan.TownsAndNations.listeners.ChatListener.Events;

import org.bukkit.entity.Player;
import org.tan.TownsAndNations.DataClass.wars.PlannedAttack;
import org.tan.TownsAndNations.listeners.ChatListener.ChatListenerEvent;
import org.tan.TownsAndNations.storage.DataStorage.PlannedAttackStorage;

import java.util.function.Consumer;

import static org.tan.TownsAndNations.enums.MessageKey.WAR_ID;

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
