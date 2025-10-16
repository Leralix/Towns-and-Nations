package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.lib.data.SoundEnum;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.dataclass.territory.cosmetic.CustomIcon;
import org.leralix.tan.enums.BrowseScope;
import org.leralix.tan.enums.RolePermission;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.legacy.PlayerGUI;
import org.leralix.tan.gui.service.requirements.RankPermissionRequirement;
import org.leralix.tan.gui.user.territory.relation.OpenDiplomacyMenu;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.ArrayList;
import java.util.List;


public abstract class TerritoryMenu extends BasicGui {

    protected final TerritoryData territoryData;

    protected TerritoryMenu(Player player, String name, TerritoryData territoryData) {
        super(player, name, 4);
        this.territoryData = territoryData;
    }

    protected GuiItem getTerritoryInfo() {

        List<String> lore = new ArrayList<>();
        lore.add(Lang.GUI_TOWN_INFO_DESC0.get(langType, territoryData.getDescription()));
        lore.add(Lang.GUI_TOWN_INFO_DESC1.get(langType, territoryData.getLeaderName()));
        lore.add(Lang.GUI_TOWN_INFO_DESC2.get(langType, Integer.toString(territoryData.getPlayerIDList().size())));
        lore.add(Lang.GUI_TOWN_INFO_DESC3.get(langType, Integer.toString(territoryData.getNumberOfClaimedChunk())));
        lore.add(territoryData.getOverlord().map(
                        overlord -> Lang.GUI_TOWN_INFO_DESC5_REGION.get(langType, overlord.getName()))
                .orElseGet(() -> Lang.GUI_TOWN_INFO_DESC5_NO_REGION.get(langType)));

        lore.add(Lang.GUI_TOWN_INFO_CHANGE_ICON.get(tanPlayer));
        lore.add(Lang.RIGHT_CLICK_TO_SELECT_MEMBER_HEAD.get(tanPlayer));

        return iconManager.get(IconKey.TERRITORY_ICON)
                .setName(Lang.GUI_TOWN_NAME.get(langType, territoryData.getName()))
                .setDescription(lore)
                .setAction(action -> {

                    if (!territoryData.doesPlayerHavePermission(tanPlayer, RolePermission.TOWN_ADMINISTRATOR)) {
                        TanChatUtils.message(player, Lang.PLAYER_NO_PERMISSION.get(langType), SoundEnum.NOT_ALLOWED);
                        return;
                    }

                    if (action.isRightClick()) {
                        new SelectTerritoryHeadMenu(player, territoryData);
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
                    TanChatUtils.message(player, Lang.GUI_TOWN_MEMBERS_ROLE_CHANGED_ICON_SUCCESS.get(langType), SoundEnum.GOOD);
                    open();
                })
                .asGuiItem(player);
    }

    protected GuiItem getTownTreasuryButton() {
        return iconManager.get(IconKey.TERRITORY_TREASURY_ICON)
                .setName(Lang.GUI_TOWN_TREASURY_ICON.get(langType))
                .setDescription(Lang.GUI_TOWN_TREASURY_ICON_DESC1.get(langType))
                .setRequirements(new RankPermissionRequirement(territoryData, tanPlayer, RolePermission.MANAGE_TAXES))
                .setAction(event -> new TreasuryMenu(player, territoryData))
                .asGuiItem(player);
    }

    protected GuiItem getMemberButton() {
        return iconManager.get(IconKey.TERRITORY_MEMBER_ICON)
                .setName(Lang.GUI_TOWN_MEMBERS_ICON.get(langType))
                .setDescription(Lang.GUI_TOWN_MEMBERS_ICON_DESC1.get(langType))
                .setAction(event -> new TerritoryMemberMenu(player, territoryData).open())
                .asGuiItem(player);
    }

    protected GuiItem getLandButton() {
        return iconManager.get(IconKey.TERRITORY_LAND_ICON)
                .setName(Lang.GUI_CLAIM_ICON.get(langType))
                .setDescription(Lang.GUI_CLAIM_ICON_DESC1.get(langType))
                .setAction(event -> new ChunkSettingsMenu(player, territoryData))
                .asGuiItem(player);
    }

    protected GuiItem getBrowseButton() {
        return iconManager.get(IconKey.TERRITORY_BROWSE_ICON)
                .setName(Lang.GUI_BROWSE_TERRITORY_ICON.get(langType))
                .setAction(event -> new BrowseTerritoryMenu(player, territoryData, BrowseScope.ALL, p -> territoryData.openMainMenu(player)))
                .asGuiItem(player);
    }

    protected GuiItem getDiplomacyButton() {
        return iconManager.get(IconKey.TERRITORY_DIPLOMACY_ICON)
                .setName(Lang.GUI_RELATION_ICON.get(langType))
                .setDescription(Lang.GUI_RELATION_ICON_DESC1.get(langType))
                .setRequirements(new RankPermissionRequirement(territoryData, tanPlayer, RolePermission.MANAGE_TOWN_RELATION))
                .setAction(event -> new OpenDiplomacyMenu(player, territoryData))
                .asGuiItem(player);
    }

    protected GuiItem getAttackButton() {
        return iconManager.get(IconKey.TERRITORY_WAR_ICON)
                .setName(Lang.GUI_ATTACK_ICON.get(langType))
                .setDescription(Lang.GUI_ATTACK_ICON_DESC1.get(langType))
                .setRequirements(new RankPermissionRequirement(territoryData, tanPlayer, RolePermission.MANAGE_WARS))
                .setAction(event -> new WarsMenu(player, territoryData))
                .asGuiItem(player);
    }

    protected GuiItem getHierarchyButton() {
        return iconManager.get(IconKey.TERRITORY_HIERARCHY_ICON)
                .setName(Lang.GUI_HIERARCHY_MENU.get(langType))
                .setDescription(Lang.GUI_HIERARCHY_MENU_DESC1.get(langType))
                .setRequirements(new RankPermissionRequirement(territoryData, tanPlayer, RolePermission.TOWN_ADMINISTRATOR))
                .setAction(event -> PlayerGUI.openHierarchyMenu(player, territoryData))
                .asGuiItem(player);
    }

    protected GuiItem getBuildingButton() {
        return iconManager.get(IconKey.TERRITORY_BUILDING_ICON)
                .setName(Lang.GUI_BUILDING_MENU.get(langType))
                .setDescription(Lang.GUI_BUILDING_MENU_DESC1.get(langType))
                .setRequirements(new RankPermissionRequirement(territoryData, tanPlayer, RolePermission.MANAGE_PROPERTY))
                .setAction(event -> new BuildingMenu(player, territoryData, this))
                .asGuiItem(player);
    }
}
