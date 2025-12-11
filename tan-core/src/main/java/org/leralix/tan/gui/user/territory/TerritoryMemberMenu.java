package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.guis.GuiItem;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.enums.RolePermission;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.gui.service.requirements.RankPermissionRequirement;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.deprecated.GuiUtil;

public class TerritoryMemberMenu extends IteratorGUI {

  private final TerritoryData territoryData;

  private TerritoryMemberMenu(Player player, ITanPlayer tanPlayer, TerritoryData territoryData) {
    super(player, tanPlayer, Lang.HEADER_TOWN_MEMBERS.get(player), 6);
    this.territoryData = territoryData;
  }

  public static void open(Player player, TerritoryData territoryData) {
    PlayerDataStorage.getInstance()
        .get(player)
        .thenAccept(
            tanPlayer -> {
              new TerritoryMemberMenu(player, tanPlayer, territoryData).open();
            });
  }

  @Override
  public void open() {
    GuiUtil.createIterator(
        gui,
        getMemberList(),
        page,
        player,
        p -> territoryData.openMainMenu(player),
        p -> nextPage(),
        p -> previousPage(),
        Material.LIME_STAINED_GLASS_PANE);

    gui.setItem(6, 4, getManageRankButton());
    if (territoryData instanceof TownData townData) {
      gui.setItem(6, 5, getManageApplicationsButton(townData));
    }
    gui.open(player);
  }

  private List<GuiItem> getMemberList() {
    return territoryData.getOrderedMemberList(tanPlayer);
  }

  private GuiItem getManageRankButton() {
    return IconManager.getInstance()
        .get(IconKey.MANAGE_RANKS_ICON)
        .setName(Lang.GUI_TOWN_MEMBERS_MANAGE_ROLES.get(tanPlayer))
        .setRequirements(
            new RankPermissionRequirement(territoryData, tanPlayer, RolePermission.MANAGE_RANKS))
        .setAction(p -> TerritoryRanksMenu.open(player, territoryData))
        .asGuiItem(player, langType);
  }

  private GuiItem getManageApplicationsButton(TownData townData) {
    return IconManager.getInstance()
        .get(IconKey.MANAGE_APPLICATIONS_ICON)
        .setName(Lang.GUI_TOWN_MEMBERS_MANAGE_APPLICATION.get(tanPlayer))
        .setRequirements(
            new RankPermissionRequirement(territoryData, tanPlayer, RolePermission.INVITE_PLAYER))
        .setDescription(
            Lang.GUI_TOWN_MEMBERS_MANAGE_APPLICATION_DESC1.get(
                Integer.toString(townData.getPlayerJoinRequestSet().size())))
        .setAction(p -> PlayerApplicationMenu.open(player, townData))
        .asGuiItem(player, langType);
  }
}
