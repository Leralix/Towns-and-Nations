package org.leralix.tan.gui.user.territory;

import static org.leralix.lib.data.SoundEnum.NOT_ALLOWED;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.enums.RolePermission;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.deprecated.GuiUtil;
import org.leralix.tan.utils.deprecated.HeadUtils;
import org.leralix.tan.utils.text.TanChatUtils;

public class PlayerApplicationMenu extends IteratorGUI {

  TownData townData;
  Map<String, ITanPlayer> applicantsData;

  private PlayerApplicationMenu(
      Player player,
      ITanPlayer tanPlayer,
      TownData townData,
      Map<String, ITanPlayer> applicantsData) {
    super(player, tanPlayer, Lang.HEADER_TOWN_APPLICATIONS.get(player), 3);
    this.townData = townData;
    this.applicantsData = applicantsData;
  }

  public static void open(Player player, TownData townData) {
    PlayerDataStorage storage = PlayerDataStorage.getInstance();

    storage
        .get(player)
        .thenCompose(
            tanPlayer -> {
              List<CompletableFuture<ITanPlayer>> applicantFutures = new ArrayList<>();
              for (String playerUUID : townData.getPlayerJoinRequestSet()) {
                applicantFutures.add(storage.get(playerUUID));
              }

              return CompletableFuture.allOf(applicantFutures.toArray(new CompletableFuture[0]))
                  .thenApply(
                      v -> {
                        Map<String, ITanPlayer> applicantsData = new HashMap<>();
                        for (CompletableFuture<ITanPlayer> future : applicantFutures) {
                          ITanPlayer applicant = future.join();
                          applicantsData.put(applicant.getID(), applicant);
                        }
                        return new Object[] {tanPlayer, applicantsData};
                      });
            })
        .thenAccept(
            data -> {
              ITanPlayer tanPlayer = (ITanPlayer) ((Object[]) data)[0];
              @SuppressWarnings("unchecked")
              Map<String, ITanPlayer> applicantsData =
                  (Map<String, ITanPlayer>) ((Object[]) data)[1];
              new PlayerApplicationMenu(player, tanPlayer, townData, applicantsData).open();
            });
  }

  @Override
  public void open() {

    GuiUtil.createIterator(
        gui,
        getApplicationList(),
        page,
        player,
        p -> TerritoryMemberMenu.open(player, townData),
        p -> nextPage(),
        p -> previousPage(),
        Material.LIME_STAINED_GLASS_PANE);
    gui.open(player);
  }

  private List<GuiItem> getApplicationList() {
    List<GuiItem> guiItems = new ArrayList<>();
    for (String playerUUID : townData.getPlayerJoinRequestSet()) {

      OfflinePlayer playerIterate = Bukkit.getOfflinePlayer(UUID.fromString(playerUUID));
      ITanPlayer playerIterateData = applicantsData.get(playerUUID);

      ItemStack playerHead =
          HeadUtils.getPlayerHead(
              playerIterate,
              Lang.GUI_PLAYER_ASK_JOIN_PROFILE_DESC2.get(tanPlayer),
              Lang.GUI_PLAYER_ASK_JOIN_PROFILE_DESC3.get(tanPlayer));

      GuiItem playerButton =
          ItemBuilder.from(playerHead)
              .asGuiItem(
                  event -> {
                    event.setCancelled(true);
                    if (event.isLeftClick()) {
                      if (!townData.doesPlayerHavePermission(
                          tanPlayer, RolePermission.INVITE_PLAYER)) {
                        TanChatUtils.message(
                            player, Lang.PLAYER_NO_PERMISSION.get(tanPlayer), NOT_ALLOWED);
                        return;
                      }
                      if (townData.isFull()) {
                        TanChatUtils.message(
                            player, Lang.INVITATION_TOWN_FULL.get(tanPlayer), NOT_ALLOWED);
                        return;
                      }
                      townData.addPlayer(playerIterateData);
                    } else if (event.isRightClick()) {
                      if (!townData.doesPlayerHavePermission(
                          tanPlayer, RolePermission.KICK_PLAYER)) {
                        TanChatUtils.message(player, Lang.PLAYER_NO_PERMISSION.get(tanPlayer));
                        return;
                      }
                      townData.removePlayerJoinRequest(playerIterateData.getID());
                    }
                    open();
                  });
      guiItems.add(playerButton);
    }
    return guiItems;
  }
}
