package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.lib.data.SoundEnum;
import org.leralix.tan.data.territory.TerritoryData;
import org.leralix.tan.data.territory.TownData;
import org.leralix.tan.data.territory.cosmetic.CustomIcon;
import org.leralix.tan.data.territory.rank.RolePermission;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.gui.scope.BrowseScope;
import org.leralix.tan.gui.service.requirements.RankPermissionRequirement;
import org.leralix.tan.gui.user.MainMenu;
import org.leralix.tan.gui.user.territory.hierarchy.HierarchyMenu;
import org.leralix.tan.gui.user.territory.relation.OpenDiplomacyMenu;
import org.leralix.tan.gui.user.territory.upgrade.UpgradeMenu;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.utils.deprecated.GuiUtil;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.List;
import java.util.function.Consumer;


public abstract class TerritoryMenu extends BasicGui {

    protected final TerritoryData territoryData;

    protected TerritoryMenu(Player player, FilledLang name, TerritoryData territoryData) {
        super(player, name, 4);
        this.territoryData = territoryData;
    }

    protected GuiItem getTerritoryInfo() {

        List<FilledLang> lore = TerritoryInfoLoreUtil.getTerritoryInfoLore(territoryData);

        return iconManager.get(IconKey.TERRITORY_ICON)
                .setName(Lang.GUI_TOWN_NAME.get(langType, territoryData.getName()))
                .setDescription(lore)
                .setClickToAcceptMessage(
                        Lang.GUI_TOWN_INFO_CHANGE_ICON,
                        Lang.RIGHT_CLICK_TO_SELECT_MEMBER_HEAD
                )
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
                .asGuiItem(player, langType);
    }

    protected GuiItem getLevelButton() {
        return IconManager.getInstance().get(IconKey.TERRITORY_LEVEL_ICON)
                .setName(Lang.GUI_TOWN_LEVEL_ICON.get(tanPlayer.getLang()))
                .setDescription(territoryData instanceof TownData ? Lang.GUI_TOWN_LEVEL_ICON_DESC1.get() : Lang.GUI_TERRITORY_LEVEL_ICON_DESC1.get())
                .setRequirements(new RankPermissionRequirement(territoryData, tanPlayer, RolePermission.UPGRADE_TOWN))
                .setAction(event -> new UpgradeMenu(player, territoryData))
                .asGuiItem(player, langType);
    }

    protected GuiItem getTownTreasuryButton() {
        return iconManager.get(IconKey.TERRITORY_TREASURY_ICON)
                .setName(Lang.GUI_TOWN_TREASURY_ICON.get(langType))
                .setDescription(territoryData instanceof TownData ? Lang.GUI_TOWN_TREASURY_ICON_DESC1.get() : Lang.GUI_TERRITORY_TREASURY_ICON_DESC1.get())
                .setRequirements(new RankPermissionRequirement(territoryData, tanPlayer, RolePermission.MANAGE_TAXES))
                .setAction(event -> new TreasuryMenu(player, territoryData))
                .asGuiItem(player, langType);
    }

    protected GuiItem getMemberButton() {
        return iconManager.get(IconKey.TERRITORY_MEMBER_ICON)
                .setName(Lang.GUI_TOWN_MEMBERS_ICON.get(langType))
                .setDescription(territoryData instanceof TownData ? Lang.GUI_TOWN_MEMBERS_ICON_DESC1.get() : Lang.GUI_TERRITORY_MEMBERS_ICON_DESC1.get())
                .setAction(event -> new TerritoryMemberMenu(player, territoryData).open())
                .asGuiItem(player, langType);
    }

    protected GuiItem getLandButton() {
        return iconManager.get(IconKey.TERRITORY_LAND_ICON)
                .setName(Lang.GUI_CLAIM_ICON.get(langType))
                .setDescription(territoryData instanceof TownData ? Lang.GUI_CLAIM_ICON_DESC1.get() : Lang.GUI_TERRITORY_CLAIM_ICON_DESC1.get())
                .setAction(event -> new ChunkSettingsMenu(player, territoryData))
                .asGuiItem(player, langType);
    }

    protected GuiItem getBrowseButton() {
        return iconManager.get(IconKey.TERRITORY_BROWSE_ICON)
                .setName(Lang.GUI_BROWSE_TERRITORY_ICON.get(langType))
                .setAction(event -> new BrowseTerritoryMenu(player, territoryData, BrowseScope.ALL, p -> territoryData.openMainMenu(player, tanPlayer)))
                .asGuiItem(player, langType);
    }

    protected GuiItem getDiplomacyButton() {
        return iconManager.get(IconKey.TERRITORY_DIPLOMACY_ICON)
                .setName(Lang.GUI_RELATION_ICON.get(langType))
                .setDescription(Lang.GUI_RELATION_ICON_DESC1.get())
                .setRequirements(new RankPermissionRequirement(territoryData, tanPlayer, RolePermission.MANAGE_TOWN_RELATION))
                .setAction(event -> new OpenDiplomacyMenu(player, territoryData))
                .asGuiItem(player, langType);
    }

    protected GuiItem getAttackButton() {
        return iconManager.get(IconKey.TERRITORY_WAR_ICON)
                .setName(Lang.GUI_ATTACK_ICON.get(langType))
                .setDescription(Lang.GUI_ATTACK_ICON_DESC1.get())
                .setRequirements(new RankPermissionRequirement(territoryData, tanPlayer, RolePermission.MANAGE_WARS))
                .setAction(event -> new WarsMenu(player, territoryData))
                .asGuiItem(player, langType);
    }

    protected GuiItem getHierarchyButton() {
        return iconManager.get(IconKey.TERRITORY_HIERARCHY_ICON)
                .setName(Lang.GUI_HIERARCHY_MENU.get(langType))
                .setDescription(Lang.GUI_HIERARCHY_MENU_DESC1.get())
                .setRequirements(new RankPermissionRequirement(territoryData, tanPlayer, RolePermission.TOWN_ADMINISTRATOR))
                .setAction(event -> new HierarchyMenu(player, territoryData))
                .asGuiItem(player, langType);
    }

    protected GuiItem getBuildingButton() {
        return iconManager.get(IconKey.TERRITORY_BUILDING_ICON)
                .setName(Lang.GUI_BUILDING_MENU.get(langType))
                .setDescription(Lang.GUI_BUILDING_MENU_DESC1.get())
                .setRequirements(new RankPermissionRequirement(territoryData, tanPlayer, RolePermission.MANAGE_PROPERTY))
                .setAction(event -> new BuildingMenu(player, territoryData, this))
                .asGuiItem(player, langType);
    }

    GuiItem createSettingsButton(FilledLang filledLang, Consumer<Player> action){
        return iconManager.get(IconKey.TERRITORY_SETTINGS_ICON)
                .setName(Lang.GUI_TOWN_SETTINGS_ICON.get(langType))
                .setDescription(filledLang)
                .setAction(event -> action.accept(player))
                .asGuiItem(player, langType);
    }

    protected void setupCommonLayout(Material glassColor) {
        gui.setItem(1, 5, getTerritoryInfo());
        gui.getFiller().fillTop(GuiUtil.getUnnamedItem(glassColor));

        gui.setItem(2, 2, getTownTreasuryButton());
        gui.setItem(2, 3, getMemberButton());
        gui.setItem(2, 5, getBrowseButton());
        gui.setItem(2, 6, getDiplomacyButton());
        gui.setItem(2, 7, getLevelButton());

        gui.setItem(3, 2, getBuildingButton());
        gui.setItem(3, 3, getAttackButton());
        gui.setItem(3, 4, getHierarchyButton());

        gui.setItem(4, 1, GuiUtil.createBackArrow(player, MainMenu::new, langType));
    }

    protected void setRow2Column4(GuiItem item) {
        gui.setItem(2, 4, item);
    }

    protected void setRow3Column8(GuiItem item) {
        gui.setItem(3, 8, item);
    }
}
