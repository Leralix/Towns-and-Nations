package org.tan.TownsAndNations.listeners.ChatListener;

import com.comphenix.protocol.PacketType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.tan.TownsAndNations.TownsAndNations;

import java.util.function.Consumer;

public abstract class ChatListenerEvent {


    public ChatListenerEvent() {
    }

    public abstract void execute(Player player, String message);

    protected static Integer parseStringToInt(String stringAmount) {
        if (stringAmount != null && stringAmount.matches("-?\\d+")) {
            return Integer.valueOf(stringAmount);
        } else {
            return null;
        }
    }

    protected void openGui(Consumer<Player> playerConsumer, Player player){
        new BukkitRunnable() {
            @Override
            public void run() {
                playerConsumer.accept(player);
            }
        }.runTask(TownsAndNations.getPlugin());
    }
}
