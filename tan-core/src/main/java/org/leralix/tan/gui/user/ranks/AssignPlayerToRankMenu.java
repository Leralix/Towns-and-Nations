package org.leralix.tan.gui.user.ranks;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
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

  private AssignPlayerToRankMenu(
      Player player, ITanPlayer tanPlayer, TerritoryData territoryData, RankData rankData) {
    super(player, tanPlayer, Lang.HEADER_RANK_ADD_PLAYER.get(tanPlayer.getLang()), 3);
    this.territoryData = territoryData;
    this.rankData = rankData;
  }

  public static void open(Player player, TerritoryData territoryData, RankData rankData) {
    PlayerDataStorage.getInstance()
        .get(player)
        .thenAccept(
            tanPlayer -> {
              new AssignPlayerToRankMenu(player, tanPlayer, territoryData, rankData).open();
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
      ITanPlayer otherITanPlayer = PlayerDataStorage.getInstance().getSync(otherPlayerUUID);

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

                    ITanPlayer playerStat =
                        PlayerDataStorage.getInstance().getSync(otherPlayerUUID);
                    territoryData.setPlayerRank(playerStat, rankData);
                    RankManagerMenu.open(player, territoryData, rankData);
                  });
      playersToAdd.add(playerInfo);
    }
    return playersToAdd;
  }
}
