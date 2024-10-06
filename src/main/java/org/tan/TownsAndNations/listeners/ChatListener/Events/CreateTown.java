package org.tan.TownsAndNations.listeners.ChatListener.Events;

import org.bukkit.entity.Player;
import org.tan.TownsAndNations.listeners.ChatListener.ChatListenerEvent;
import org.tan.TownsAndNations.utils.TownUtil;

public class CreateTown extends ChatListenerEvent {
    int cost;
    public CreateTown(int cost) {
        super();
        this.cost = cost;
    }

    @Override
    public void execute(Player player, String message) {
        TownUtil.createTown(player, cost, message);
    }
}
