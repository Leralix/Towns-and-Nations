package org.leralix.tan.gui.user;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.dataclass.territory.cosmetic.CustomIcon;
import org.leralix.tan.enums.BrowseScope;
import org.leralix.tan.enums.RolePermission;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.gui.legacy.PlayerGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.GuiUtil;
import org.leralix.tan.utils.TanChatUtils;

import java.util.ArrayList;
import java.util.List;

import static org.leralix.lib.data.SoundEnum.NOT_ALLOWED;

public class TownMenu extends BasicGui {

    TownData townData;

    public TownMenu(Player player) {
        super(player, Lang.HEADER_TOWN_MENU.get(PlayerDataStorage.getInstance().get(player).getTown().getName()), 4);
        townData = playerData.getTown();
    }

    @Override
    public void open() {

        gui.setItem(1, 5, getTownIcon());

        gui.setItem(2, 2, getTownTreasuryButton());
        gui.setItem(2, 3, getMemberButton());
        gui.setItem(2, 4, getLandButton());
        gui.setItem(2, 5, getBrowseButton());
        gui.setItem(2, 6, getDiplomacyButton());
        gui.setItem(2, 7, getLevelButton());
        gui.setItem(2, 8, getSettingsButton());

        gui.setItem(3, 2, getPropertiesButton());
        gui.setItem(3, 3, getLandmarksButton());
        gui.setItem(3, 4, getAttackButton());
        gui.setItem(3, 5, getHierarchyButton());

        gui.setItem(4, 1, GuiUtil.createBackArrow(player, p -> new MainMenu(p).open()));

        gui.open(player);
    }

    private GuiItem getTownIcon() {

        LangType langType = playerData.getLang();

        List<String> lore = new ArrayList<>();
        lore.add(Lang.GUI_TOWN_INFO_DESC0.get(langType, townData.getDescription()));
        lore.add(Lang.GUI_TOWN_INFO_DESC1.get(langType, townData.getLeaderName()));
        lore.add(Lang.GUI_TOWN_INFO_DESC2.get(langType, townData.getPlayerIDList().size()));
        lore.add(Lang.GUI_TOWN_INFO_DESC3.get(langType, townData.getNumberOfClaimedChunk()));
        lore.add(townData.haveOverlord() ? Lang.GUI_TOWN_INFO_DESC5_REGION.get(langType, townData.getOverlord().getName()) : Lang.GUI_TOWN_INFO_DESC5_NO_REGION.get(langType));
        lore.add(Lang.GUI_TOWN_INFO_CHANGE_ICON.get(playerData));
        lore.add(Lang.RIGHT_CLICK_TO_SELECT_MEMBER_HEAD.get(playerData));

        return IconManager.getInstance().get(IconKey.TOWN_ICON)
                .setName(Lang.GUI_TOWN_NAME.get(langType, townData.getName()))
                .setDescription(lore)
                .setAction( action -> {
                    if(action.getCursor() == null){
                        return;
                    }
                    if(action.getCursor().getType() == Material.AIR){
                        return;
                    }
                    if(!townData.doesPlayerHavePermission(playerData, RolePermission.TOWN_ADMINISTRATOR)){
                        player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION.get(langType));
                        SoundUtil.playSound(player,NOT_ALLOWED);
                        return;
                    }
                    ItemStack itemMaterial = action.getCursor();
                    if(itemMaterial.getType() == Material.AIR && action.isRightClick()){
                        PlayerGUI.openSelectHeadTerritoryMenu(player, townData, 0);
                    }
                    else {
                        townData.setIcon(new CustomIcon(itemMaterial));
                        player.sendMessage(TanChatUtils.getTANString() + Lang.GUI_TOWN_MEMBERS_ROLE_CHANGED_ICON_SUCCESS.get(langType));
                        new TownMenu(player).open();
                    }
                })
                .asGuiItem(player);
    }

    private GuiItem getTownTreasuryButton() {
        return IconManager.getInstance().get(IconKey.TOWN_TREASURY_ICON)
                .setName(Lang.GUI_TOWN_TREASURY_ICON.get(playerData.getLang()))
                .setDescription(
                        Lang.GUI_TOWN_TREASURY_ICON_DESC1.get(playerData.getLang()),
                        Lang.GUI_GENERIC_CLICK_TO_OPEN.get(playerData)
                )
                .setAction(event -> PlayerGUI.openTreasury(player, townData))
                .asGuiItem(player);
    }

    private GuiItem getMemberButton() {
        return IconManager.getInstance().get(IconKey.TOWN_MEMBER_ICON)
                .setName(Lang.GUI_TOWN_MEMBERS_ICON.get(playerData.getLang()))
                .setDescription(Lang.GUI_TOWN_MEMBERS_ICON_DESC1.get(playerData.getLang()),
                        Lang.GUI_GENERIC_CLICK_TO_OPEN.get(playerData)
                )
                .setAction(event -> new TerritoryMemberMenu(player, townData).open())
                .asGuiItem(player);
    }

    private GuiItem getLandButton() {
        return IconManager.getInstance().get(IconKey.TOWN_LAND_ICON)
                .setName(Lang.GUI_CLAIM_ICON.get(playerData.getLang()))
                .setDescription(
                        Lang.GUI_CLAIM_ICON_DESC1.get(playerData.getLang()),
                        Lang.GUI_GENERIC_CLICK_TO_OPEN.get(playerData)
                )
                .setAction(event -> PlayerGUI.openChunkSettings(player, townData))
                .asGuiItem(player);
    }

    private GuiItem getBrowseButton() {
        return IconManager.getInstance().get(IconKey.TOWN_BROWSE_ICON)
                .setName(Lang.GUI_BROWSE_TERRITORY_ICON.get(playerData.getLang()))
                .setDescription(
                        Lang.GUI_BROWSE_TERRITORY_ICON_DESC1.get(playerData.getLang()),
                        Lang.GUI_GENERIC_CLICK_TO_OPEN.get(playerData)
                )
                .setAction(event -> PlayerGUI.browseTerritory(player, townData, BrowseScope.TOWNS, p -> new TownMenu(player).open(), 0))
                .asGuiItem(player);
    }

    private GuiItem getDiplomacyButton() {
        return IconManager.getInstance().get(IconKey.TOWN_DIPLOMACY_ICON)
                .setName(Lang.GUI_RELATION_ICON.get(playerData.getLang()))
                .setDescription(
                        Lang.GUI_RELATION_ICON_DESC1.get(playerData.getLang()),
                        Lang.GUI_GENERIC_CLICK_TO_OPEN.get(playerData)
                )
                .setAction(event -> PlayerGUI.openRelations(player, townData))
                .asGuiItem(player);
    }

    private GuiItem getLevelButton() {
        return IconManager.getInstance().get(IconKey.TOWN_LEVEL_ICON)
                .setName(Lang.GUI_TOWN_LEVEL_ICON.get(playerData.getLang()))
                .setDescription(
                        Lang.GUI_TOWN_LEVEL_ICON_DESC1.get(playerData.getLang()),
                        Lang.GUI_GENERIC_CLICK_TO_OPEN.get(playerData)
                )
                .setAction(event -> PlayerGUI.openTownLevel(player, 0))
                .asGuiItem(player);
    }

    private GuiItem getSettingsButton() {
        return IconManager.getInstance().get(IconKey.TOWN_SETTINGS_ICON)
                .setName(Lang.GUI_TOWN_SETTINGS_ICON.get(playerData.getLang()))
                .setDescription(
                        Lang.GUI_TOWN_SETTINGS_ICON_DESC1.get(playerData.getLang()),
                        Lang.GUI_GENERIC_CLICK_TO_OPEN.get(playerData)
                )
                .setAction(event -> PlayerGUI.openTownSettings(player, townData))
                .asGuiItem(player);
    }

    private GuiItem getPropertiesButton() {
        return IconManager.getInstance().get(IconKey.TOWN_PROPERTIES_ICON)
                .setName(Lang.GUI_TOWN_PROPERTIES_ICON.get(playerData.getLang()))
                .setDescription(
                        Lang.GUI_TOWN_PROPERTIES_ICON_DESC1.get(playerData.getLang()),
                        Lang.GUI_GENERIC_CLICK_TO_OPEN.get(playerData)
                )
                .setAction(event -> PlayerGUI.openTownPropertiesMenu(player, 0))
                .asGuiItem(player);
    }

    private GuiItem getLandmarksButton() {
        return IconManager.getInstance().get(IconKey.TOWN_LANDMARKS_ICON)
                .setName(Lang.ADMIN_GUI_LANDMARK_ICON.get(playerData.getLang()))
                .setDescription(
                        Lang.ADMIN_GUI_LANDMARK_DESC1.get(playerData.getLang()),
                        Lang.GUI_GENERIC_CLICK_TO_OPEN.get(playerData)
                )
                .setAction(event -> PlayerGUI.openOwnedLandmark(player, townData, 0))
                .asGuiItem(player);
    }

    private GuiItem getAttackButton() {
        return IconManager.getInstance().get(IconKey.TOWN_WAR_ICON)
                .setName(Lang.GUI_ATTACK_ICON.get(playerData.getLang()))
                .setDescription(
                        Lang.GUI_ATTACK_ICON_DESC1.get(playerData.getLang()),
                        Lang.GUI_GENERIC_CLICK_TO_OPEN.get(playerData)
                )
                .setAction(event -> PlayerGUI.openWarMenu(player, townData, 0))
                .asGuiItem(player);
    }

    private GuiItem getHierarchyButton() {
        return IconManager.getInstance().get(IconKey.TOWN_HIERARCHY_ICON)
                .setName(Lang.GUI_HIERARCHY_MENU.get(playerData.getLang()))
                .setDescription(
                        Lang.GUI_HIERARCHY_MENU_DESC1.get(playerData.getLang()),
                        Lang.GUI_GENERIC_CLICK_TO_OPEN.get(playerData)
                )
                .setAction(event -> PlayerGUI.openHierarchyMenu(player, townData))
                .asGuiItem(player);
    }
}
