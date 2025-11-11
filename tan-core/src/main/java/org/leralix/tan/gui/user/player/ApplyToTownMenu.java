package org.leralix.tan.gui.user.player;

import static org.leralix.lib.data.SoundEnum.NOT_ALLOWED;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.gui.user.territory.NoTownMenu;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.deprecated.GuiUtil;
import org.leralix.tan.utils.deprecated.HeadUtils;
import org.leralix.tan.utils.text.TanChatUtils;

public class ApplyToTownMenu extends IteratorGUI {

  private ApplyToTownMenu(Player player, ITanPlayer tanPlayer) {
    super(player, tanPlayer, Lang.HEADER_TOWN_LIST.get(tanPlayer.getLang()), 6);
  }

  public static void open(Player player) {
    PlayerDataStorage.getInstance()
        .get(player)
        .thenAccept(
            tanPlayer -> {
              new ApplyToTownMenu(player, tanPlayer).open();
            });
  }

  @Override
  public void open() {

    GuiUtil.createIterator(
        gui, getTowns(), page, player, NoTownMenu::open, p -> nextPage(), p -> previousPage());

    gui.open(player);
  }

  public List<GuiItem> getTowns() {
    ArrayList<GuiItem> towns = new ArrayList<>();

    for (TownData specificTownData : TownDataStorage.getInstance().getAllSync().values()) {
      ItemStack townIcon = specificTownData.getIconWithInformations(tanPlayer.getLang());
      HeadUtils.addLore(
          townIcon,
          "",
          (specificTownData.isRecruiting())
              ? Lang.GUI_TOWN_INFO_IS_RECRUITING.get(tanPlayer)
              : Lang.GUI_TOWN_INFO_IS_NOT_RECRUITING.get(tanPlayer),
          (specificTownData.isPlayerAlreadyRequested(player))
              ? Lang.GUI_TOWN_INFO_RIGHT_CLICK_TO_CANCEL.get(tanPlayer)
              : Lang.GUI_TOWN_INFO_LEFT_CLICK_TO_JOIN.get(tanPlayer));
      GuiItem townButton =
          ItemBuilder.from(townIcon)
              .asGuiItem(
                  event -> {
                    event.setCancelled(true);

                    if (event.isLeftClick()) {

                      if (!player.hasPermission("tan.base.town.join")) {
                        TanChatUtils.message(
                            player, Lang.PLAYER_NO_PERMISSION.get(tanPlayer), NOT_ALLOWED);
                        return;
                      }
                      if (specificTownData.isPlayerAlreadyRequested(player)) {
                        return;
                      }
                      if (!specificTownData.isRecruiting()) {
                        TanChatUtils.message(
                            player, Lang.PLAYER_TOWN_NOT_RECRUITING.get(tanPlayer));
                        return;
                      }
                      specificTownData.addPlayerJoinRequest(player);
                      TanChatUtils.message(
                          player,
                          Lang.PLAYER_ASK_TO_JOIN_TOWN_PLAYER_SIDE.get(
                              tanPlayer, specificTownData.getName()));
                      open();
                    }
                    if (event.isRightClick()) {
                      if (!specificTownData.isPlayerAlreadyRequested(player)) {
                        return;
                      }
                      specificTownData.removePlayerJoinRequest(player);
                      TanChatUtils.message(
                          player, Lang.PLAYER_REMOVE_ASK_TO_JOIN_TOWN_PLAYER_SIDE.get(tanPlayer));
                      open();
                    }
                  });
      towns.add(townButton);
    }
    return towns;
  }
}
