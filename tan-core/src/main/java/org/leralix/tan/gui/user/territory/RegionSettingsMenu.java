package org.leralix.tan.gui.user.territory;

import static org.leralix.lib.data.SoundEnum.GOOD;
import static org.leralix.lib.data.SoundEnum.NOT_ALLOWED;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.events.EventManager;
import org.leralix.tan.events.events.RegionDeletednternalEvent;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.user.MainMenu;
import org.leralix.tan.gui.utils.ConfirmMenu;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.deprecated.GuiUtil;
import org.leralix.tan.utils.file.FileUtil;
import org.leralix.tan.utils.text.TanChatUtils;

public class RegionSettingsMenu extends SettingsMenus {

  private final RegionData regionData;

  public RegionSettingsMenu(Player player, ITanPlayer tanPlayer, RegionData regionData) {
    super(player, tanPlayer, Lang.HEADER_SETTINGS.get(player), regionData, 3);
    this.regionData = regionData;
    // open() doit être appelé explicitement après la construction pour respecter le modèle
    // asynchrone
  }

  public static void open(Player player, RegionData regionData) {
    PlayerDataStorage.getInstance()
        .get(player)
        .thenAccept(
            tanPlayer -> {
              new RegionSettingsMenu(player, tanPlayer, regionData).open();
            });
  }

  @Override
  public void open() {
    gui.setItem(1, 5, getTerritoryInfo());
    gui.getFiller().fillTop(GuiUtil.getUnnamedItem(Material.LIGHT_BLUE_STAINED_GLASS_PANE));

    gui.setItem(2, 2, getRenameButton());
    gui.setItem(2, 3, getChangeDescriptionButton());
    gui.setItem(2, 4, getChangeColorButton());

    gui.setItem(2, 6, getChangeOwnershipButton());
    gui.setItem(2, 7, getDeleteButton());

    gui.setItem(3, 1, GuiUtil.createBackArrow(player, p -> RegionMenu.open(player, regionData)));

    gui.open(player);
  }

  private @NotNull GuiItem getChangeOwnershipButton() {
    return iconManager
        .get(IconKey.REGION_CHANGE_OWNERSHIP_ICON)
        .setName(Lang.GUI_REGION_CHANGE_CAPITAL.get(tanPlayer))
        .setDescription(
            Lang.GUI_REGION_CHANGE_CAPITAL_DESC1.get(regionData.getCapital().getName()),
            Lang.GUI_REGION_CHANGE_CAPITAL_DESC2.get())
        .setAction(
            event -> {
              event.setCancelled(true);
              if (!regionData.isLeader(tanPlayer)) {
                TanChatUtils.message(player, Lang.GUI_NEED_TO_BE_LEADER_OF_REGION.get(tanPlayer));
                return;
              }
              // TODO: Implement region change ownership GUI after PlayerGUI migration
              // Original: PlayerGUI.openRegionChangeOwnership(player, 0)
              TanChatUtils.message(player, Lang.PLAYER_NO_PERMISSION.get(tanPlayer), NOT_ALLOWED);
            })
        .asGuiItem(player, langType);
  }

  private GuiItem getDeleteButton() {
    return iconManager
        .get(IconKey.REGION_DELETE_REGION_ICON)
        .setName(Lang.GUI_REGION_DELETE.get(tanPlayer))
        .setDescription(
            Lang.GUI_REGION_DELETE_DESC1.get(regionData.getName()),
            Lang.GUI_REGION_DELETE_DESC2.get(),
            Lang.GUI_REGION_DELETE_DESC3.get())
        .setAction(
            event -> {
              event.setCancelled(true);
              if (!regionData.isLeader(tanPlayer)) {
                TanChatUtils.message(player, Lang.GUI_NEED_TO_BE_LEADER_OF_REGION.get(tanPlayer));
                return;
              }
              if (regionData.isCapital()) {
                TanChatUtils.message(
                    player,
                    Lang.CANNOT_DELETE_TERRITORY_IF_CAPITAL.get(
                        tanPlayer, regionData.getOverlord().get().getBaseColoredName()));
                return;
              }

              if (!player.hasPermission("tan.base.region.disband")) {
                TanChatUtils.message(player, Lang.PLAYER_NO_PERMISSION.get(tanPlayer), NOT_ALLOWED);
                return;
              }

              ConfirmMenu.open(
                  player,
                  Lang.GUI_CONFIRM_DELETE_REGION.get(),
                  p -> {
                    FileUtil.addLineToHistory(
                        Lang.REGION_DELETED_NEWSLETTER.get(player.getName(), regionData.getName()));
                    EventManager.getInstance()
                        .callEvent(new RegionDeletednternalEvent(regionData, tanPlayer));
                    regionData.delete();
                    SoundUtil.playSound(player, GOOD);
                    MainMenu.open(player);
                  },
                  p -> open());
            })
        .asGuiItem(player, langType);
  }
}
