package org.leralix.tan.gui.user.ranks;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.RankData;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.deprecated.GuiUtil;
import org.leralix.tan.utils.deprecated.HeadUtils;
import org.leralix.tan.utils.text.TanChatUtils;

public class AssignPlayerToRankMenu extends IteratorGUI {

  private final TerritoryData territoryData;
  private final RankData rankData;
  private final Map<String, ITanPlayer> playersData;

  private AssignPlayerToRankMenu(
      Player player,
      ITanPlayer tanPlayer,
      TerritoryData territoryData,
      RankData rankData,
      Map<String, ITanPlayer> playersData) {
    super(player, tanPlayer, Lang.HEADER_RANK_ADD_PLAYER.get(tanPlayer.getLang()), 3);
    this.territoryData = territoryData;
    this.rankData = rankData;
    this.playersData = playersData;
  }

  public static void open(Player player, TerritoryData territoryData, RankData rankData) {
    PlayerDataStorage storage = PlayerDataStorage.getInstance();

    storage
        .get(player)
        .thenCompose(
            tanPlayer -> {
              List<CompletableFuture<ITanPlayer>> playerFutures = new ArrayList<>();
              for (String playerID : territoryData.getPlayerIDList()) {
                playerFutures.add(storage.get(playerID));
              }

              return CompletableFuture.allOf(playerFutures.toArray(new CompletableFuture[0]))
                  .thenApply(
                      v -> {
                        Map<String, ITanPlayer> playersData = new HashMap<>();
                        for (CompletableFuture<ITanPlayer> future : playerFutures) {
                          ITanPlayer playerData = future.join();
                          playersData.put(playerData.getID(), playerData);
                        }
                        return new Object[] {tanPlayer, playersData};
                      });
            })
        .thenAccept(
            data -> {
              ITanPlayer tanPlayer = (ITanPlayer) ((Object[]) data)[0];
              @SuppressWarnings("unchecked")
              Map<String, ITanPlayer> playersData = (Map<String, ITanPlayer>) ((Object[]) data)[1];
              new AssignPlayerToRankMenu(player, tanPlayer, territoryData, rankData, playersData)
                  .open();
            });
  }

  @Override
  public void open() {
    GuiUtil.createIterator(
        gui,
        getAvailablePlayers(),
        page,
        player,
        p -> RankManagerMenu.open(p, territoryData, rankData),
        p -> nextPage(),
        p -> previousPage());

    gui.open(player);
  }

  private List<GuiItem> getAvailablePlayers() {
    List<GuiItem> playersToAdd = new ArrayList<>();
    for (String otherPlayerUUID : territoryData.getPlayerIDList()) {
      ITanPlayer otherITanPlayer = playersData.get(otherPlayerUUID);

      if (Objects.equals(otherITanPlayer.getRankID(territoryData), rankData.getID())) {
        continue;
      }
      ItemStack playerHead =
          HeadUtils.getPlayerHead(
              otherITanPlayer.getNameStored(),
              Bukkit.getOfflinePlayer(UUID.fromString(otherPlayerUUID)));
      GuiItem playerInfo =
          ItemBuilder.from(playerHead)
              .asGuiItem(
                  event -> {
                    event.setCancelled(true);
                    RankData otherPlayerActualRank = territoryData.getRank(otherITanPlayer);
                    if (territoryData.getRank(player).getLevel() <= otherPlayerActualRank.getLevel()
                        && !territoryData.isLeader(tanPlayer)) {
                      TanChatUtils.message(
                          player, Lang.PLAYER_NO_PERMISSION_RANK_DIFFERENCE.get(tanPlayer));
                      return;
                    }

                    ITanPlayer playerStat = playersData.get(otherPlayerUUID);
                    territoryData.setPlayerRank(playerStat, rankData);
                    RankManagerMenu.open(player, territoryData, rankData);
                  });
      playersToAdd.add(playerInfo);
    }
    return playersToAdd;
  }
}
