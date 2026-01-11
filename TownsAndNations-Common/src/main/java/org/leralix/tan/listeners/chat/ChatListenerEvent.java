package org.leralix.tan.listeners.chat;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.function.Consumer;

public abstract class ChatListenerEvent {


    protected ChatListenerEvent() {

    }

    protected abstract boolean execute(Player player, String message);

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

    protected static boolean checkMessageLength(Player player, String message, int minSize, int maxSize) {
        if (message.length() < minSize) {
            TanChatUtils.message(player, Lang.MESSAGE_TOO_SHORT.get(player, Integer.toString(minSize)));
            return true;
        }

        if (message.length() > maxSize) {
            TanChatUtils.message(player, Lang.MESSAGE_TOO_LONG.get(player, Integer.toString(maxSize)));
            return true;
        }
        return false;
    }
}
