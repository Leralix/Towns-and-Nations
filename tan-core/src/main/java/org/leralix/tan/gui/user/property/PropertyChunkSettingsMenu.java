package org.leralix.tan.gui.user.property;

import dev.triumphteam.gui.guis.GuiItem;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.PropertyData;
import org.leralix.tan.dataclass.territory.permission.RelationPermission;
import org.leralix.tan.enums.permissions.ChunkPermissionType;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.PermissionManager;
import org.leralix.tan.storage.stored.PlayerDataStorage;

public class PropertyChunkSettingsMenu extends IteratorGUI {

  private final PropertyData propertyData;
  private final BasicGui returnMenu;

  private PropertyChunkSettingsMenu(
      Player player, ITanPlayer tanPlayer, PropertyData propertyData, BasicGui returnGui) {
    super(player, tanPlayer, Lang.HEADER_CHUNK_PERMISSION.get(tanPlayer.getLang()), 4);
    this.propertyData = propertyData;
    this.returnMenu = returnGui;
  }

  public static void open(Player player, PropertyData propertyData, BasicGui returnGui) {
    PlayerDataStorage.getInstance()
        .get(player)
        .thenAccept(
            tanPlayer -> {
              new PropertyChunkSettingsMenu(player, tanPlayer, propertyData, returnGui).open();
            });
  }

  @Override
  public void open() {
    iterator(getChunkPermission(), p -> returnMenu.open(), Material.LIME_STAINED_GLASS_PANE);

    gui.open(player);
  }

  private List<GuiItem> getChunkPermission() {
    List<GuiItem> guiItems = new ArrayList<>();

    PermissionManager permissionManager = propertyData.getPermissionManager();

    for (ChunkPermissionType type : ChunkPermissionType.values()) {
      RelationPermission permission = permissionManager.get(type).getOverallPermission();

      GuiItem item =
          iconManager
              .get(type.getIconKey())
              .setName(type.getName().get(tanPlayer))
              .setDescription(
                  Lang.GUI_TOWN_CLAIM_SETTINGS_DESC1.get(permission.getColoredName(langType)))
              .setClickToAcceptMessage(
                  Lang.GUI_GENERIC_CLICK_TO_MODIFY, Lang.GUI_RIGHT_CLICK_TO_ADD_SPECIFIC_PLAYER)
              .setAction(
                  event -> {
                    event.setCancelled(true);
                    if (event.isLeftClick()) {
                      permissionManager.nextPermission(type);
                      open();
                    } else if (event.isRightClick()) {
                      BrowsePlayerWithPermissionMenu.open(player, permissionManager, type, this);
                    }
                  })
              .asGuiItem(player, langType);

      guiItems.add(item);
    }
    return guiItems;
  }
}
