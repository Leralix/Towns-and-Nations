package org.leralix.tan.gui.user;

import dev.triumphteam.gui.guis.GuiItem;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.leralix.lib.data.SoundEnum;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.user.player.PlayerMenu;
import org.leralix.tan.gui.user.territory.NoRegionMenu;
import org.leralix.tan.gui.user.territory.NoTownMenu;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.timezone.TimeZoneManager;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.deprecated.GuiUtil;
import org.leralix.tan.utils.text.TanChatUtils;

public class MainMenu extends BasicGui {

  @Nullable private final TownData townData;
  @Nullable private final RegionData regionData;

  private MainMenu(
      Player player,
      ITanPlayer tanPlayer,
      @Nullable TownData townData,
      @Nullable RegionData regionData) {
    super(player, tanPlayer, Lang.HEADER_MAIN_MENU.get(tanPlayer.getLang()), 3);
    this.townData = townData;
    this.regionData = regionData;
  }

  /**
   * Opens the main menu asynchronously.
   *
   * @param player The player viewing the menu
   */
  public static void open(Player player) {
    PlayerDataStorage.getInstance()
        .get(player)
        .thenCompose(
            tanPlayer -> {
              // Pre-load town and region data if player has them
              CompletableFuture<TownData> townFuture =
                  tanPlayer.hasTown()
                      ? tanPlayer.getTown()
                      : CompletableFuture.completedFuture(null);

              CompletableFuture<RegionData> regionFuture =
                  tanPlayer.hasRegion()
                      ? tanPlayer.getRegion()
                      : CompletableFuture.completedFuture(null);

              // Wait for both to complete
              return CompletableFuture.allOf(townFuture, regionFuture)
                  .thenApply(v -> new Object[] {tanPlayer, townFuture.join(), regionFuture.join()});
            })
        .thenAccept(
            data -> {
              ITanPlayer tanPlayer = (ITanPlayer) ((Object[]) data)[0];
              TownData townData = (TownData) ((Object[]) data)[1];
              RegionData regionData = (RegionData) ((Object[]) data)[2];
              new MainMenu(player, tanPlayer, townData, regionData).open();
            });
  }

  @Override
  public void open() {

    gui.setItem(1, 5, getTimeIcon());

    int nationPosition = 2;
    int regionPosition = 4;
    int townPosition = 6;
    int playerPosition = 8;

    if (Constants.enableRegion()) {
      if (Constants.enableNation()) {
        gui.setItem(2, nationPosition, getNationButton(tanPlayer));
      } else {
        regionPosition = 3;
        townPosition = 5;
        playerPosition = 7;
      }
      gui.setItem(2, regionPosition, getRegionButton(tanPlayer));
    } else {
      townPosition = 4;
      playerPosition = 6;
    }

    gui.setItem(2, townPosition, getTownButton(tanPlayer));
    gui.setItem(2, playerPosition, getPlayerButton(tanPlayer));

    gui.setItem(3, 1, GuiUtil.createBackArrow(player, HumanEntity::closeInventory));

    gui.open(player);
  }

  private @NotNull GuiItem getTimeIcon() {

    TimeZoneManager timeManager = TimeZoneManager.getInstance();

    IconKey icon =
        timeManager.isDayForServer() ? IconKey.TIMEZONE_ICON_DAY : IconKey.TIMEZONE_ICON_NIGHT;

    return iconManager
        .get(icon)
        .setName(Lang.GUI_SERVER_TIME.get(tanPlayer))
        .setDescription(
            Lang.CURRENT_SERVER_TIME.get(timeManager.formatDateNowForServer().get(langType)),
            Lang.CURRENT_PLAYER_TIME.get(
                timeManager.formatDateNowForPlayer(tanPlayer).get(langType)))
        .asGuiItem(player, langType);
  }

  private GuiItem getNationButton(ITanPlayer tanPlayer) {
    return iconManager
        .get(IconKey.NATION_BASE_ICON)
        .setName(Lang.GUI_KINGDOM_ICON.get(tanPlayer))
        .setDescription(Lang.GUI_WARNING_STILL_IN_DEV.get())
        .setAction(
            action ->
                TanChatUtils.message(
                    player, Lang.GUI_WARNING_STILL_IN_DEV.get(tanPlayer), SoundEnum.NOT_ALLOWED))
        .asGuiItem(player, langType);
  }

  private GuiItem getRegionButton(ITanPlayer tanPlayer) {

    List<FilledLang> description = new ArrayList<>();

    if (regionData != null) {
      description.add(Lang.GUI_REGION_ICON_DESC1_REGION.get(regionData.getColoredName()));
      description.add(
          Lang.GUI_REGION_ICON_DESC2_REGION.get(regionData.getRank(tanPlayer).getColoredName()));
    } else {
      description.add(Lang.GUI_REGION_ICON_DESC1_NO_REGION.get());
    }

    return iconManager
        .get(IconKey.REGION_BASE_ICON)
        .setName(Lang.GUI_REGION_ICON.get(tanPlayer))
        .setDescription(description)
        .setAction(
            action -> {
              // TODO: Replace with proper region menu navigation after PlayerGUI migration
              // Original: PlayerGUI.dispatchPlayerRegion(player)
              if (regionData != null) {
                regionData.openMainMenu(player);
              } else {
                NoRegionMenu.open(player);
              }
            })
        .asGuiItem(player, langType);
  }

  private GuiItem getTownButton(ITanPlayer tanPlayer) {

    List<FilledLang> description = new ArrayList<>();
    if (townData != null) {
      description.add(Lang.GUI_TOWN_ICON_DESC1_HAVE_TOWN.get(townData.getColoredName()));
      description.add(
          Lang.GUI_TOWN_ICON_DESC2_HAVE_TOWN.get(townData.getRank(tanPlayer).getColoredName()));
    } else {
      description.add(Lang.GUI_TOWN_ICON_DESC1_NO_TOWN.get());
    }

    return iconManager
        .get(IconKey.TOWN_BASE_ICON)
        .setName(Lang.GUI_TOWN_ICON.get(tanPlayer))
        .setDescription(description)
        .setAction(
            action -> {
              // TODO: Replace with proper town menu navigation after PlayerGUI migration
              // Original: PlayerGUI.dispatchPlayerTown(player)
              if (townData != null) {
                townData.openMainMenu(player);
              } else {
                NoTownMenu.open(player);
              }
            })
        .asGuiItem(player, langType);
  }

  private GuiItem getPlayerButton(ITanPlayer tanPlayer) {
    return iconManager
        .get(IconKey.PLAYER_BASE_ICON)
        .setName(Lang.GUI_PLAYER_MENU_ICON.get(tanPlayer, player.getName()))
        .setAction(action -> PlayerMenu.open(player))
        .asGuiItem(player, langType);
  }
}
