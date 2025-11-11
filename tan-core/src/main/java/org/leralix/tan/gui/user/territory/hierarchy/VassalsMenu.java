package org.leralix.tan.gui.user.territory.hierarchy;

import static org.leralix.lib.data.SoundEnum.BAD;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.enums.RolePermission;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.gui.utils.ConfirmMenu;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.deprecated.HeadUtils;
import org.leralix.tan.utils.text.TanChatUtils;

public class VassalsMenu extends IteratorGUI {

  private final TerritoryData territoryData;

  private VassalsMenu(Player player, ITanPlayer tanPlayer, TerritoryData territoryData) {
    super(player, tanPlayer, Lang.HEADER_VASSALS.get(tanPlayer.getLang()), 4);
    this.territoryData = territoryData;
  }

  public static void open(Player player, TerritoryData territoryData) {
    PlayerDataStorage.getInstance()
        .get(player)
        .thenAccept(
            tanPlayer -> {
              new VassalsMenu(player, tanPlayer, territoryData).open();
            });
  }

  @Override
  public void open() {
    iterator(getVassals(), p -> territoryData.openMainMenu(player));

    gui.setItem(4, 3, getAddVassalButton());

    gui.open(player);
  }

  private @NotNull GuiItem getAddVassalButton() {
    ItemStack addTown =
        HeadUtils.makeSkullB64(
            Lang.GUI_INVITE_TOWN_TO_REGION.get(tanPlayer),
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWZmMzE0MzFkNjQ1ODdmZjZlZjk4YzA2NzU4MTA2ODFmOGMxM2JmOTZmNTFkOWNiMDdlZDc4NTJiMmZmZDEifX19");

    return ItemBuilder.from(addTown)
        .asGuiItem(
            event -> {
              event.setCancelled(true);
              if (!territoryData.doesPlayerHavePermission(
                  tanPlayer, RolePermission.TOWN_ADMINISTRATOR)) {
                TanChatUtils.message(player, Lang.GUI_NEED_TO_BE_LEADER_OF_REGION.get(tanPlayer));
                return;
              }
              AddVassalMenu.open(player, territoryData);
            });
  }

  private List<GuiItem> getVassals() {

    List<GuiItem> res = new ArrayList<>();

    for (TerritoryData vassal : territoryData.getVassals()) {

      GuiItem vassalButton =
          iconManager
              .get(vassal.getIcon())
              .setName(vassal.getColoredName())
              .setDescription()
              .setDescription(
                  Lang.GUI_TOWN_INFO_DESC0.get(vassal.getDescription()),
                  Lang.GUI_TOWN_INFO_DESC1.get(vassal.getLeaderNameSync()),
                  Lang.GUI_TOWN_INFO_DESC2.get(Integer.toString(vassal.getPlayerIDList().size())),
                  Lang.GUI_TOWN_INFO_DESC3.get(Integer.toString(vassal.getNumberOfClaimedChunk())))
              .setClickToAcceptMessage(Lang.GUI_GENERIC_RIGHT_CLICK_TO_REMOVE)
              .setAction(
                  action -> {
                    if (!action.isRightClick()) {
                      return;
                    }
                    if (vassal.isCapital()) {
                      TanChatUtils.message(
                          player, Lang.CANT_KICK_REGIONAL_CAPITAL.get(tanPlayer, vassal.getName()));
                      return;
                    }
                    ConfirmMenu.open(
                        player,
                        Lang.GUI_REGION_KICK_TOWN_DESC1.get(),
                        p -> {
                          territoryData.broadcastMessageWithSound(
                              Lang.GUI_REGION_KICK_TOWN_BROADCAST.get(vassal.getName()), BAD);
                          vassal.removeOverlord();
                          open();
                        },
                        p -> open());
                  })
              .asGuiItem(player, langType);
      res.add(vassalButton);
    }
    return res;
  }
}
