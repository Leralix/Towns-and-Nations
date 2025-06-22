package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.dataclass.territory.cosmetic.CustomIcon;
import org.leralix.tan.enums.RolePermission;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.gui.legacy.PlayerGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.listeners.chat.PlayerChatListenerStorage;
import org.leralix.tan.listeners.chat.events.ChangeColor;
import org.leralix.tan.listeners.chat.events.ChangeTerritoryDescription;
import org.leralix.tan.listeners.chat.events.ChangeTerritoryName;
import org.leralix.tan.utils.Constants;
import org.leralix.tan.utils.TanChatUtils;

import java.util.ArrayList;
import java.util.List;

import static org.leralix.lib.data.SoundEnum.NOT_ALLOWED;

public abstract class SettingsMenus extends BasicGui {

    protected final TerritoryData territoryData;

    public SettingsMenus(Player player, String title, TerritoryData territoryData) {
        super(player, title, 3);
        this.territoryData = territoryData;
    }

    protected GuiItem getTerritoryInfo() {

        LangType langType = ITanPlayer.getLang();

        List<String> lore = new ArrayList<>();
        lore.add(Lang.GUI_TOWN_INFO_DESC0.get(langType, territoryData.getDescription()));
        lore.add(Lang.GUI_TOWN_INFO_DESC1.get(langType, territoryData.getLeaderName()));
        lore.add(Lang.GUI_TOWN_INFO_DESC2.get(langType, territoryData.getPlayerIDList().size()));
        lore.add(Lang.GUI_TOWN_INFO_DESC3.get(langType, territoryData.getNumberOfClaimedChunk()));
        lore.add(territoryData.haveOverlord() ? Lang.GUI_TOWN_INFO_DESC5_REGION.get(langType, territoryData.getOverlord().getName()) : Lang.GUI_TOWN_INFO_DESC5_NO_REGION.get(langType));
        lore.add(Lang.GUI_TOWN_INFO_CHANGE_ICON.get(ITanPlayer));
        lore.add(Lang.RIGHT_CLICK_TO_SELECT_MEMBER_HEAD.get(ITanPlayer));

        return IconManager.getInstance().get(IconKey.TERRITORY_ICON)
                .setName(Lang.GUI_TOWN_NAME.get(langType, territoryData.getName()))
                .setDescription(lore)
                .asGuiItem(player);
    }

    protected GuiItem getRenameButton() {

        int cost = Constants.getChangeTerritoryNameCost(territoryData);

        return iconManager.get(IconKey.TERRITORY_RENAME_ICON)
                .setName(Lang.GUI_TOWN_SETTINGS_LEAVE_TOWN.get(ITanPlayer))
                .setDescription(
                        Lang.GUI_TOWN_SETTINGS_CHANGE_TOWN_NAME_DESC1.get(ITanPlayer, territoryData.getName()),
                        Lang.GUI_TOWN_SETTINGS_CHANGE_TOWN_NAME_DESC2.get(ITanPlayer),
                        Lang.GUI_TOWN_SETTINGS_CHANGE_TOWN_NAME_DESC3.get(ITanPlayer, cost))
                .setAction(action -> {
                    player.sendMessage(TanChatUtils.getTANString() + Lang.GUI_TOWN_SETTINGS_CHANGE_MESSAGE_IN_CHAT.get(ITanPlayer));
                    PlayerChatListenerStorage.register(player, new ChangeTerritoryName(territoryData, cost, p -> open()));
                })
                .asGuiItem(player);
    }

    protected GuiItem getChangeDescriptionButton() {
        return iconManager.get(IconKey.TERRITORY_DESCRIPTION_ICON)
                .setName(Lang.GUI_TOWN_SETTINGS_CHANGE_TOWN_MESSAGE.get(ITanPlayer))
                .setDescription(
                        Lang.GUI_TOWN_SETTINGS_CHANGE_TOWN_MESSAGE_DESC1.get(ITanPlayer, territoryData.getDescription())
                )
                .setAction(action -> {
                    player.sendMessage(TanChatUtils.getTANString() + Lang.GUI_TOWN_SETTINGS_CHANGE_MESSAGE_IN_CHAT.get(ITanPlayer));
                    PlayerChatListenerStorage.register(player, new ChangeTerritoryDescription(territoryData, p -> open()));
                })
                .asGuiItem(player);
    }

    protected GuiItem getChangeColorButton() {
        return iconManager.get(IconKey.TERRITORY_CHANGE_COLOR_ICON)
                .setName(Lang.GUI_TOWN_SETTINGS_CHANGE_CHUNK_COLOR.get(ITanPlayer))
                .setDescription(
                        Lang.GUI_TOWN_SETTINGS_CHANGE_CHUNK_COLOR_DESC1.get(ITanPlayer),
                        Lang.GUI_TOWN_SETTINGS_CHANGE_CHUNK_COLOR_DESC2.get(ITanPlayer, territoryData.getChunkColor() + territoryData.getChunkColorInHex()),
                        Lang.GUI_TOWN_SETTINGS_CHANGE_CHUNK_COLOR_DESC3.get(ITanPlayer)
                )
                .setAction(action -> {
                    if (!territoryData.doesPlayerHavePermission(ITanPlayer, RolePermission.TOWN_ADMINISTRATOR)) {
                        player.sendMessage(TanChatUtils.getTANString() + Lang.NOT_TOWN_LEADER_ERROR.get(ITanPlayer));
                    } else {
                        player.sendMessage(TanChatUtils.getTANString() + Lang.GUI_TOWN_SETTINGS_WRITE_NEW_COLOR_IN_CHAT.get(ITanPlayer));
                        PlayerChatListenerStorage.register(player, new ChangeColor(territoryData, p -> open()));
                    }

                })
                .asGuiItem(player);
    }


}
