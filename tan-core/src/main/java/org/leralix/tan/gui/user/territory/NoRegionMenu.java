package org.leralix.tan.gui.user.territory;

import static org.leralix.lib.data.SoundEnum.NOT_ALLOWED;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.enums.BrowseScope;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.user.MainMenu;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.chat.PlayerChatListenerStorage;
import org.leralix.tan.listeners.chat.events.CreateRegion;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.RegionDataStorage;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.deprecated.GuiUtil;
import org.leralix.tan.utils.text.TanChatUtils;

public class NoRegionMenu extends BasicGui {

  private NoRegionMenu(Player player, ITanPlayer tanPlayer) {
    super(player, tanPlayer, Lang.HEADER_NO_REGION.get(player), 3);
  }

  public static void open(Player player) {
    PlayerDataStorage.getInstance()
        .get(player)
        .thenAccept(
            tanPlayer -> {
              new NoRegionMenu(player, tanPlayer).open();
            });
  }

  @Override
  public void open() {

    gui.setItem(2, 3, getCreateRegionButton());
    gui.setItem(2, 7, getBrowseRegionsButton());
    gui.setItem(3, 1, GuiUtil.createBackArrow(player, p -> MainMenu.open(player)));

    gui.open(player);
  }

  private GuiItem getCreateRegionButton() {

    int regionCost = Constants.getRegionCost();

    return iconManager
        .get(IconKey.CREATE_REGION_ICON)
        .setName(Lang.GUI_REGION_CREATE.get(tanPlayer))
        .setDescription(
            Lang.GUI_REGION_CREATE_DESC1.get(Integer.toString(regionCost)),
            Lang.GUI_REGION_CREATE_DESC2.get())
        .setAction(
            action -> {
              if (!player.hasPermission("tan.base.region.create")) {
                TanChatUtils.message(player, Lang.PLAYER_NO_PERMISSION.get(tanPlayer), NOT_ALLOWED);
                return;
              }

              if (!tanPlayer.hasTown()) {
                TanChatUtils.message(player, Lang.PLAYER_NO_TOWN.get(tanPlayer), NOT_ALLOWED);
                return;
              }
              tanPlayer
                  .getTown()
                  .thenAccept(
                      townData -> {
                        if (townData == null) return;
                        double townMoney = townData.getBalance();
                        if (townMoney < regionCost) {
                          TanChatUtils.message(
                              player,
                              Lang.TERRITORY_NOT_ENOUGH_MONEY.get(
                                  tanPlayer,
                                  townData.getColoredName(),
                                  Double.toString(regionCost - townMoney)));
                        } else {
                          TanChatUtils.message(
                              player, Lang.WRITE_IN_CHAT_NEW_REGION_NAME.get(tanPlayer));
                          PlayerChatListenerStorage.register(player, new CreateRegion(regionCost));
                        }
                      });
            })
        .asGuiItem(player, langType);
  }

  private GuiItem getBrowseRegionsButton() {
    return iconManager
        .get(IconKey.BROWSE_REGION_ICON)
        .setName(Lang.GUI_REGION_BROWSE.get(tanPlayer))
        .setDescription(
            Lang.GUI_REGION_BROWSE_DESC1.get(
                Integer.toString(RegionDataStorage.getInstance().getAllSync().size())),
            Lang.GUI_REGION_BROWSE_DESC2.get())
        .setAction(
            action -> {
              BrowseTerritoryMenu.open(player, null, BrowseScope.REGIONS, p -> open());
            })
        .asGuiItem(player, langType);
  }
}
