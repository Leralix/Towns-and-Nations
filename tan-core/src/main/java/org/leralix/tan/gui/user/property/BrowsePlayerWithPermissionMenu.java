package org.leralix.tan.gui.user.property;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.enums.permissions.ChunkPermissionType;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.PermissionManager;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.deprecated.HeadUtils;

public class BrowsePlayerWithPermissionMenu extends IteratorGUI {

  private final PermissionManager permissionManager;
  private final ChunkPermissionType chunkPermission;
  private final BasicGui returnMenu;

  private BrowsePlayerWithPermissionMenu(
      Player player,
      ITanPlayer tanPlayer,
      PermissionManager permissionManager,
      ChunkPermissionType permission,
      BasicGui returnMenu) {
    super(player, tanPlayer, permission.getLabel(tanPlayer.getLang()), 3);
    this.permissionManager = permissionManager;
    this.chunkPermission = permission;
    this.returnMenu = returnMenu;
  }

  public static void open(
      Player player,
      PermissionManager permissionManager,
      ChunkPermissionType permission,
      BasicGui returnMenu) {
    PlayerDataStorage.getInstance()
        .get(player)
        .thenAccept(
            tanPlayer -> {
              new BrowsePlayerWithPermissionMenu(
                      player, tanPlayer, permissionManager, permission, returnMenu)
                  .open();
            });
  }

  @Override
  public void open() {
    iterator(getAuthorizedPlayers(), p -> returnMenu.open());

    gui.setItem(3, 5, getAddPlayerButton());

    gui.open(player);
  }

  private GuiItem getAddPlayerButton() {
    return iconManager
        .get(IconKey.ADD_PLAYER_ICON)
        .setName(Lang.GUI_GENERIC_ADD_BUTTON.get(tanPlayer))
        .setAction(
            event -> {
              event.setCancelled(true);
              AddPlayerWithPermissionMenu.open(player, permissionManager, chunkPermission, this);
            })
        .asGuiItem(player, langType);
  }

  private List<GuiItem> getAuthorizedPlayers() {
    List<GuiItem> guiItems = new ArrayList<>();

    for (String authorizedPlayerID :
        permissionManager.get(chunkPermission).getAuthorizedPlayers()) {
      OfflinePlayer authorizedPlayer = Bukkit.getOfflinePlayer(UUID.fromString(authorizedPlayerID));
      ItemStack icon =
          HeadUtils.getPlayerHead(
              authorizedPlayer.getName(),
              authorizedPlayer,
              Lang.GUI_TOWN_MEMBER_DESC3.get(tanPlayer));

      GuiItem guiItem =
          ItemBuilder.from(icon)
              .asGuiItem(
                  event -> {
                    event.setCancelled(true);

                    if (event.isRightClick()) {
                      permissionManager
                          .get(chunkPermission)
                          .removeSpecificPlayerPermission(authorizedPlayerID);
                      open();
                    }
                  });
      guiItems.add(guiItem);
    }
    return guiItems;
  }
}
