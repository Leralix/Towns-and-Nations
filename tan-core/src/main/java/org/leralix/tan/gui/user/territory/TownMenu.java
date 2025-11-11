package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.gui.user.MainMenu;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.deprecated.GuiUtil;
import org.leralix.tan.utils.text.TanChatUtils;

public class TownMenu extends TerritoryMenu {

  private final TownData townData;

  private TownMenu(Player player, ITanPlayer tanPlayer, TownData townData) {
    super(
        player,
        tanPlayer,
        Lang.HEADER_TOWN_MENU.get(tanPlayer.getLang(), townData.getName()),
        townData);
    this.townData = townData;
  }

  public static void open(Player player, TownData townData) {
    PlayerDataStorage.getInstance()
        .get(player)
        .thenAccept(
            tanPlayer -> {
              new TownMenu(player, tanPlayer, townData).open();
            });
  }

  @Override
  public void open() {
    gui.setItem(1, 5, getTerritoryInfo());
    gui.getFiller().fillTop(GuiUtil.getUnnamedItem(Material.BLUE_STAINED_GLASS_PANE));

    gui.setItem(2, 2, getTownTreasuryButton());
    gui.setItem(2, 3, getMemberButton());
    gui.setItem(2, 4, getLandButton());
    gui.setItem(2, 5, getBrowseButton());
    gui.setItem(2, 6, getDiplomacyButton());
    gui.setItem(2, 7, getLevelButton());
    gui.setItem(2, 8, getSettingsButton());

    gui.setItem(3, 2, getBuildingButton());
    gui.setItem(3, 3, getAttackButton());
    gui.setItem(3, 4, getHierarchyButton());

    gui.setItem(3, 8, getLandmarksButton());

    gui.setItem(4, 1, GuiUtil.createBackArrow(player, MainMenu::open));

    gui.open(player);
  }

  private GuiItem getSettingsButton() {
    return IconManager.getInstance()
        .get(IconKey.TERRITORY_SETTINGS_ICON)
        .setName(Lang.GUI_TOWN_SETTINGS_ICON.get(tanPlayer.getLang()))
        .setDescription(Lang.GUI_TOWN_SETTINGS_ICON_DESC1.get())
        .setAction(event -> TownSettingsMenu.open(player, townData))
        .asGuiItem(player, langType);
  }

  private GuiItem getLandmarksButton() {
    return IconManager.getInstance()
        .get(IconKey.TOWN_LANDMARKS_ICON)
        .setName(Lang.ADMIN_GUI_LANDMARK_ICON.get(tanPlayer.getLang()))
        .setDescription(Lang.ADMIN_GUI_LANDMARK_DESC1.get())
        .setAction(
            event -> {
              // TODO: Implement owned landmark GUI after PlayerGUI migration
              // Original: PlayerGUI.openOwnedLandmark(player, townData, 0)
              TanChatUtils.message(player, Lang.PLAYER_NO_PERMISSION.get(tanPlayer.getLang()));
            })
        .asGuiItem(player, langType);
  }
}
