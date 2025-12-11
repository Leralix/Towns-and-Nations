package org.leralix.tan.listeners.chat.events;

import org.bukkit.entity.Player;
import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.api.internal.wrappers.TanPlayerWrapper;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.events.EventManager;
import org.leralix.tan.events.events.RegionCreatedInternalEvent;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.chat.ChatListenerEvent;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.RegionDataStorage;
import org.leralix.tan.utils.text.TanChatUtils;

public class CreateRegion extends ChatListenerEvent {

  private final int cost;

  public CreateRegion(int cost) {
    super();
    this.cost = cost;
  }

  @Override
  public boolean execute(Player player, String message) {

    PlayerDataStorage.getInstance()
        .get(player)
        .thenAccept(
            tanPlayer -> {
              tanPlayer
                  .getTown()
                  .thenAccept(
                      town -> {
                        if (!town.isLeader(player)) {
                          TanChatUtils.message(
                              player, Lang.PLAYER_ONLY_LEADER_CAN_PERFORM_ACTION.get(tanPlayer));
                          return;
                        }

                        if (town.getBalance() < cost) {
                          TanChatUtils.message(
                              player,
                              Lang.TERRITORY_NOT_ENOUGH_MONEY.get(
                                  tanPlayer,
                                  town.getColoredName(),
                                  Double.toString(cost - town.getBalance())));
                          return;
                        }

                        int maxSize =
                            ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("RegionNameSize");
                        if (message.length() > maxSize) {
                          TanChatUtils.message(
                              player,
                              Lang.MESSAGE_TOO_LONG.get(tanPlayer, Integer.toString(maxSize)));
                          return;
                        }

                        if (RegionDataStorage.getInstance().isNameUsed(message)) {
                          TanChatUtils.message(player, Lang.NAME_ALREADY_USED.get(tanPlayer));
                          return;
                        }

                        createRegion(player, message, town, tanPlayer);
                      });
            });
    return true;
  }

  private void createRegion(
      Player player, String regionName, TownData capital, ITanPlayer playerData) {
    capital.removeFromBalance(cost);
    RegionDataStorage.getInstance()
        .createNewRegion(regionName, capital)
        .thenAccept(
            newRegion -> {
              EventManager.getInstance()
                  .callEvent(
                      new RegionCreatedInternalEvent(newRegion, TanPlayerWrapper.of(playerData)));
              openGui(p -> newRegion.openMainMenu(player), player);
            });
  }
}
