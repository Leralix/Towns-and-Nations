package org.leralix.tan.listeners.chat;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.leralix.tan.TownsAndNations;

import java.util.function.Consumer;

public abstract class ChatListenerEvent {


    protected ChatListenerEvent() {

    }

    protected abstract void execute(Player player, String message);

    protected static Integer parseStringToInt(String stringAmount) {
        if (stringAmount != null && stringAmount.matches("-?\\d+")) {
            return Integer.valueOf(stringAmount);
        } else {
            return null;
        }
    }

    protected static Double parseStringToDouble(String stringAmount) {

        if (stringAmount != null && stringAmount.matches("-?\\d+(\\.\\d+)?")) {
            return Double.valueOf(stringAmount);
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
