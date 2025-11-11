package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.guis.GuiItem;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.lib.data.SoundEnum;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.dataclass.territory.cosmetic.CustomIcon;
import org.leralix.tan.enums.BrowseScope;
import org.leralix.tan.enums.RolePermission;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.gui.service.requirements.RankPermissionRequirement;
import org.leralix.tan.gui.user.territory.relation.OpenDiplomacyMenu;
import org.leralix.tan.gui.user.territory.upgrade.UpgradeMenu;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.utils.text.TanChatUtils;

public abstract class TerritoryMenu extends BasicGui {

  protected final TerritoryData territoryData;

  protected TerritoryMenu(
      Player player, ITanPlayer tanPlayer, String name, TerritoryData territoryData) {
    super(player, tanPlayer, name, 4);
    this.territoryData = territoryData;
  }

  protected GuiItem getTerritoryInfo() {

    List<FilledLang> lore = new ArrayList<>();
    lore.add(Lang.GUI_TOWN_INFO_DESC0.get(territoryData.getDescription()));
    lore.add(Lang.GUI_TOWN_INFO_DESC1.get(territoryData.getLeaderNameSync()));
    lore.add(
        Lang.GUI_TOWN_INFO_DESC2.get(Integer.toString(territoryData.getPlayerIDList().size())));
    lore.add(
        Lang.GUI_TOWN_INFO_DESC3.get(Integer.toString(territoryData.getNumberOfClaimedChunk())));
    lore.add(
        territoryData
            .getOverlord()
            .map(overlord -> Lang.GUI_TOWN_INFO_DESC5_REGION.get(overlord.getName()))
            .orElseGet(Lang.GUI_TOWN_INFO_DESC5_NO_REGION::get));
    lore.add(Lang.GUI_TOWN_INFO_CHANGE_ICON.get());
    lore.add(Lang.RIGHT_CLICK_TO_SELECT_MEMBER_HEAD.get());

    return iconManager
        .get(IconKey.TERRITORY_ICON)
        .setName(Lang.GUI_TOWN_NAME.get(langType, territoryData.getName()))
        .setDescription(lore)
        .setAction(
            action -> {
              if (!territoryData.doesPlayerHavePermission(
                  tanPlayer, RolePermission.TOWN_ADMINISTRATOR)) {
                TanChatUtils.message(
                    player, Lang.PLAYER_NO_PERMISSION.get(langType), SoundEnum.NOT_ALLOWED);
                return;
              }

              if (action.isRightClick()) {
                SelectTerritoryHeadMenu.open(player, territoryData);
                return;
              }

              if (action.getCursor() == null) {
                return;
              }
              if (action.getCursor().getType() == Material.AIR) {
                return;
              }

              ItemStack itemMaterial = action.getCursor();
              territoryData.setIcon(new CustomIcon(itemMaterial));
              TanChatUtils.message(
                  player,
                  Lang.GUI_TOWN_MEMBERS_ROLE_CHANGED_ICON_SUCCESS.get(langType),
                  SoundEnum.GOOD);
              open();
            })
        .asGuiItem(player, langType);
  }

  protected GuiItem getLevelButton() {
    return IconManager.getInstance()
        .get(IconKey.TERRITORY_LEVEL_ICON)
        .setName(Lang.GUI_TOWN_LEVEL_ICON.get(tanPlayer.getLang()))
        .setDescription(Lang.GUI_TOWN_LEVEL_ICON_DESC1.get())
        .setRequirements(
            new RankPermissionRequirement(territoryData, tanPlayer, RolePermission.UPGRADE_TOWN))
        .setAction(event -> UpgradeMenu.open(player, territoryData))
        .asGuiItem(player, langType);
  }

  protected GuiItem getTownTreasuryButton() {
    return iconManager
        .get(IconKey.TERRITORY_TREASURY_ICON)
        .setName(Lang.GUI_TOWN_TREASURY_ICON.get(langType))
        .setDescription(Lang.GUI_TOWN_TREASURY_ICON_DESC1.get())
        .setRequirements(
            new RankPermissionRequirement(territoryData, tanPlayer, RolePermission.MANAGE_TAXES))
        .setAction(event -> TreasuryMenu.open(player, territoryData))
        .asGuiItem(player, langType);
  }

  protected GuiItem getMemberButton() {
    return iconManager
        .get(IconKey.TERRITORY_MEMBER_ICON)
        .setName(Lang.GUI_TOWN_MEMBERS_ICON.get(langType))
        .setDescription(Lang.GUI_TOWN_MEMBERS_ICON_DESC1.get())
        .setAction(event -> TerritoryMemberMenu.open(player, territoryData))
        .asGuiItem(player, langType);
  }

  protected GuiItem getLandButton() {
    return iconManager
        .get(IconKey.TERRITORY_LAND_ICON)
        .setName(Lang.GUI_CLAIM_ICON.get(langType))
        .setDescription(Lang.GUI_CLAIM_ICON_DESC1.get())
        .setAction(event -> ChunkSettingsMenu.open(player, territoryData))
        .asGuiItem(player, langType);
  }

  protected GuiItem getBrowseButton() {
    return iconManager
        .get(IconKey.TERRITORY_BROWSE_ICON)
        .setName(Lang.GUI_BROWSE_TERRITORY_ICON.get(langType))
        .setAction(
            event -> {
              BrowseTerritoryMenu browseMenu =
                  new BrowseTerritoryMenu(
                      player,
                      tanPlayer,
                      territoryData,
                      BrowseScope.ALL,
                      p -> territoryData.openMainMenu(player));
              browseMenu.open();
            })
        .asGuiItem(player, langType);
  }

  protected GuiItem getDiplomacyButton() {
    return iconManager
        .get(IconKey.TERRITORY_DIPLOMACY_ICON)
        .setName(Lang.GUI_RELATION_ICON.get(langType))
        .setDescription(Lang.GUI_RELATION_ICON_DESC1.get())
        .setRequirements(
            new RankPermissionRequirement(
                territoryData, tanPlayer, RolePermission.MANAGE_TOWN_RELATION))
        .setAction(event -> OpenDiplomacyMenu.open(player, territoryData))
        .asGuiItem(player, langType);
  }

  protected GuiItem getAttackButton() {
    return iconManager
        .get(IconKey.TERRITORY_WAR_ICON)
        .setName(Lang.GUI_ATTACK_ICON.get(langType))
        .setDescription(Lang.GUI_ATTACK_ICON_DESC1.get())
        .setRequirements(
            new RankPermissionRequirement(territoryData, tanPlayer, RolePermission.MANAGE_WARS))
        .setAction(event -> WarsMenu.open(player, territoryData))
        .asGuiItem(player, langType);
  }

  protected GuiItem getHierarchyButton() {
    return iconManager
        .get(IconKey.TERRITORY_HIERARCHY_ICON)
        .setName(Lang.GUI_HIERARCHY_MENU.get(langType))
        .setDescription(Lang.GUI_HIERARCHY_MENU_DESC1.get())
        .setRequirements(
            new RankPermissionRequirement(
                territoryData, tanPlayer, RolePermission.TOWN_ADMINISTRATOR))
        .setAction(
            event ->
                org.leralix.tan.gui.user.territory.hierarchy.VassalsMenu.open(
                    player, territoryData))
        .asGuiItem(player, langType);
  }

  protected GuiItem getBuildingButton() {
    return iconManager
        .get(IconKey.TERRITORY_BUILDING_ICON)
        .setName(Lang.GUI_BUILDING_MENU.get(langType))
        .setDescription(Lang.GUI_BUILDING_MENU_DESC1.get())
        .setRequirements(
            new RankPermissionRequirement(territoryData, tanPlayer, RolePermission.MANAGE_PROPERTY))
        .setAction(
            event -> {
              BuildingMenu buildingMenu = new BuildingMenu(player, tanPlayer, territoryData, this);
              buildingMenu.open();
            })
        .asGuiItem(player, langType);
  }
}
