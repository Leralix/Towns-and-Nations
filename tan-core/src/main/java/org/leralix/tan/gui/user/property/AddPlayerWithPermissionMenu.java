package org.leralix.tan.gui.user.property;

import static org.leralix.lib.data.SoundEnum.ADD;

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
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.permission.ChunkPermission;
import org.leralix.tan.enums.permissions.ChunkPermissionType;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.PermissionManager;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.deprecated.HeadUtils;

public class AddPlayerWithPermissionMenu extends IteratorGUI {

  private final PermissionManager permissionManager;
  private final ChunkPermissionType chunkPermission;
  private final BrowsePlayerWithPermissionMenu returnMenu;
  private final Map<String, ITanPlayer> playersData;

  private AddPlayerWithPermissionMenu(
      Player player,
      ITanPlayer tanPlayer,
      PermissionManager permissionManager,
      ChunkPermissionType chunkPermission,
      BrowsePlayerWithPermissionMenu browsePlayerWithPermissionMenu,
      Map<String, ITanPlayer> playersData) {
    super(player, tanPlayer, chunkPermission.getLabel(tanPlayer.getLang()), 3);

    this.permissionManager = permissionManager;
    this.chunkPermission = chunkPermission;
    this.returnMenu = browsePlayerWithPermissionMenu;
    this.playersData = playersData;
  }

  public static void open(
      Player player,
      PermissionManager permissionManager,
      ChunkPermissionType chunkPermission,
      BrowsePlayerWithPermissionMenu browsePlayerWithPermissionMenu) {
    PlayerDataStorage storage = PlayerDataStorage.getInstance();

    storage
        .get(player)
        .thenCompose(
            tanPlayer -> {
              // Load all online players data in parallel
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
              new AddPlayerWithPermissionMenu(
                      player,
                      tanPlayer,
                      permissionManager,
                      chunkPermission,
                      browsePlayerWithPermissionMenu,
                      playersData)
                  .open();
            });
  }

  @Override
  public void open() {
    iterator(
        getPlayers(),
        player -> {
          returnMenu.open();
        });

    gui.open(player);
  }

  private List<GuiItem> getPlayers() {

    List<GuiItem> guiItems = new ArrayList<>();
    for (Player playerToAdd : Bukkit.getOnlinePlayers()) {

      ITanPlayer playerToAddData = playersData.get(playerToAdd.getUniqueId().toString());
      ChunkPermission permission = permissionManager.get(chunkPermission);
      // Check with town since only town can have territories
      if (permission.isAllowed(tanPlayer.getTownSync(), playerToAddData)) continue;

      ItemStack icon =
          HeadUtils.getPlayerHead(
              playerToAdd.getName(), playerToAdd, Lang.GUI_GENERIC_ADD_BUTTON.get(tanPlayer));

      GuiItem guiItem =
          ItemBuilder.from(icon)
              .asGuiItem(
                  event -> {
                    event.setCancelled(true);

                    permission.addSpecificPlayerPermission(playerToAdd.getUniqueId().toString());
                    SoundUtil.playSound(player, ADD);
                    open();
                  });
      guiItems.add(guiItem);
    }
    return guiItems;
  }
}
