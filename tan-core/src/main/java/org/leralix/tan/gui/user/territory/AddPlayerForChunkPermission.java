package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.enums.RolePermission;
import org.leralix.tan.enums.permissions.ChunkPermissionType;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.deprecated.HeadUtils;
import org.leralix.tan.utils.text.TanChatUtils;

public class AddPlayerForChunkPermission extends IteratorGUI {

  private final TerritoryData territoryData;
  private final ChunkPermissionType type;
  private final BasicGui backMenu;
  private final Map<String, ITanPlayer> playersData;

  private AddPlayerForChunkPermission(
      Player player,
      ITanPlayer tanPlayer,
      TerritoryData territoryData,
      ChunkPermissionType type,
      BasicGui backMenu,
      Map<String, ITanPlayer> playersData) {
    super(player, tanPlayer, Lang.HEADER_AUTHORIZE_PLAYER.get(player), 6);
    this.territoryData = territoryData;
    this.type = type;
    this.backMenu = backMenu;
    this.playersData = playersData;
  }

  /**
   * Opens the add player permission menu asynchronously.
   *
   * @param player The player viewing the menu
   * @param territoryData The territory to manage permissions for
   * @param type The type of chunk permission
   * @param backMenu The menu to return to
   */
  public static void open(
      Player player, TerritoryData territoryData, ChunkPermissionType type, BasicGui backMenu) {
    PlayerDataStorage storage = PlayerDataStorage.getInstance();

    // Load viewer data and all online players data
    storage
        .get(player)
        .thenCompose(
            tanPlayer -> {
              List<CompletableFuture<ITanPlayer>> playerFutures = new ArrayList<>();
              for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                playerFutures.add(storage.get(onlinePlayer));
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
              new AddPlayerForChunkPermission(
                      player, tanPlayer, territoryData, type, backMenu, playersData)
                  .open();
            });
  }

  @Override
  public void open() {
    iterator(getPeopleToAuthorized(), p -> backMenu.open());
    gui.open(player);
  }

  private List<GuiItem> getPeopleToAuthorized() {
    ITanPlayer playerStat = playersData.get(player.getUniqueId().toString());

    List<GuiItem> guiItems = new ArrayList<>();

    for (Player playerToAdd : Bukkit.getOnlinePlayers()) {

      ITanPlayer playerToAddData = playersData.get(playerToAdd.getUniqueId().toString());
      if (territoryData.getPermission(type).isAllowed(territoryData, playerToAddData)) continue;

      ItemStack icon =
          HeadUtils.getPlayerHead(
              playerToAdd.getName(), playerToAdd, Lang.GUI_GENERIC_ADD_BUTTON.get(tanPlayer));

      GuiItem guiItem =
          ItemBuilder.from(icon)
              .asGuiItem(
                  event -> {
                    event.setCancelled(true);
                    if (!territoryData.doesPlayerHavePermission(
                        playerStat, RolePermission.MANAGE_CLAIM_SETTINGS)) {
                      TanChatUtils.message(player, Lang.PLAYER_NO_PERMISSION.get(tanPlayer));
                      return;
                    }
                    territoryData
                        .getPermission(type)
                        .addSpecificPlayerPermission(playerToAdd.getUniqueId().toString());
                    open();
                    SoundUtil.playSound(player, SoundEnum.ADD);
                  });
      guiItems.add(guiItem);
    }

    return guiItems;
  }
}
