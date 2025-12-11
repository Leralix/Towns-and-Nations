package org.leralix.tan.listeners.chat.events;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.economy.EconomyUtil;
import org.leralix.tan.events.EventManager;
import org.leralix.tan.events.events.TownCreatedInternalEvent;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.chat.ChatListenerEvent;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.file.FileUtil;
import org.leralix.tan.utils.graphic.TeamUtils;
import org.leralix.tan.utils.text.TanChatUtils;

public class CreateTown extends ChatListenerEvent {
  int cost;

  public CreateTown(int cost) {
    super();
    this.cost = cost;
  }

  @Override
  public boolean execute(Player player, String message) {
    double playerBalance = EconomyUtil.getBalance(player);

    if (playerBalance < cost) {
      TanChatUtils.message(
          player,
          Lang.PLAYER_NOT_ENOUGH_MONEY_EXTENDED.get(player, Double.toString(cost - playerBalance)));
      return false;
    }

    FileConfiguration config = ConfigUtil.getCustomConfig(ConfigTag.MAIN);
    int maxSize = config.getInt("TownNameSize", 45);

    if (message.length() > maxSize) {
      TanChatUtils.message(player, Lang.MESSAGE_TOO_LONG.get(player, Integer.toString(maxSize)));
      return false;
    }

    if (TownDataStorage.getInstance().isNameUsed(message)) {
      TanChatUtils.message(player, Lang.NAME_ALREADY_USED.get(player));
      return false;
    }
    createTown(player, message);

    return true;
  }

  public void createTown(Player player, String message) {
    PlayerDataStorage.getInstance()
        .get(player)
        .thenAccept(
            tanPlayer -> {
              TownDataStorage.getInstance()
                  .newTown(message, tanPlayer)
                  .thenAccept(
                      newTown -> {
                        EconomyUtil.removeFromBalance(player, cost);
                        EventManager.getInstance()
                            .callEvent(new TownCreatedInternalEvent(newTown, tanPlayer));
                        FileUtil.addLineToHistory(
                            Lang.TOWN_CREATED_NEWSLETTER.get(player.getName(), newTown.getName()));

                        org.leralix.tan.utils.FoliaScheduler.runTask(
                            TownsAndNations.getPlugin(),
                            () -> TeamUtils.setIndividualScoreBoard(player));

                        openGui(p -> newTown.openMainMenu(player), player);
                      });
            });
  }
}
