package org.leralix.tan.gui.admin;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.deprecated.GuiUtil;

public class AdminMainMenu extends BasicGui {

  private AdminMainMenu(Player player, ITanPlayer tanPlayer) {
    super(player, tanPlayer, Lang.HEADER_ADMIN_MAIN_MENU.get(tanPlayer.getLang()), 3);
  }

  public static void open(Player player) {
    PlayerDataStorage.getInstance()
        .get(player)
        .thenAccept(
            tanPlayer -> {
              new AdminMainMenu(player, tanPlayer).open();
            });
  }

  @Override
  public void open() {

    gui.setItem(2, 2, getRegionButton());
    gui.setItem(2, 3, getTownButton());
    gui.setItem(2, 4, getPlayerButton());
    gui.setItem(2, 7, getLandmarkButton());
    gui.setItem(2, 8, getWarButton());

    gui.setItem(3, 1, GuiUtil.createBackArrow(player, p -> player.closeInventory()));

    gui.open(player);
  }

  private GuiItem getWarButton() {
    return iconManager
        .get(IconKey.TERRITORY_WAR_ICON)
        .setName(Lang.ADMIN_GUI_WAR_ICON.get(langType))
        .setAction(action -> AdminWarsMenu.open(player))
        .asGuiItem(player, langType);
  }

  private GuiItem getLandmarkButton() {
    return iconManager
        .get(IconKey.TOWN_LANDMARKS_ICON)
        .setName(Lang.ADMIN_GUI_LANDMARK_ICON.get(langType))
        .setAction(action -> AdminLandmarkMenu.open(player))
        .asGuiItem(player, langType);
  }

  private GuiItem getPlayerButton() {
    return iconManager
        .get(IconKey.PLAYER_HEAD_ICON)
        .setName(Lang.GUI_ADMIN_PLAYER_ICON.get(langType))
        .setAction(action -> AdminPlayerMenu.open(player))
        .asGuiItem(player, langType);
  }

  private GuiItem getTownButton() {
    return iconManager
        .get(IconKey.TOWN_BASE_ICON)
        .setName(Lang.GUI_TOWN_ICON.get(langType))
        .setAction(action -> AdminBrowseTownMenu.open(player))
        .asGuiItem(player, langType);
  }

  private GuiItem getRegionButton() {
    return iconManager
        .get(IconKey.REGION_BASE_ICON)
        .setName(Lang.GUI_REGION_ICON.get(langType))
        .setAction(action -> AdminBrowseRegionMenu.open(player))
        .asGuiItem(player, langType);
  }
}
