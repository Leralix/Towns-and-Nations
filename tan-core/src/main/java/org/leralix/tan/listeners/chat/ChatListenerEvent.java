package org.leralix.tan.listeners.chat;

import java.util.function.Consumer;
import org.bukkit.entity.Player;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.utils.FoliaScheduler;

public abstract class ChatListenerEvent {

  protected ChatListenerEvent() {}

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

  protected void openGui(Consumer<Player> playerConsumer, Player player) {
    FoliaScheduler.runTask(TownsAndNations.getPlugin(), () -> playerConsumer.accept(player));
  }
}
