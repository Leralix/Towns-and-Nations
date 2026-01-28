package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Tag;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.data.building.fort.Fort;
import org.leralix.tan.data.territory.TerritoryData;
import org.leralix.tan.data.territory.rank.RolePermission;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.gui.service.requirements.RankPermissionRequirement;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.listeners.chat.PlayerChatListenerStorage;
import org.leralix.tan.listeners.chat.events.ChangeColor;
import org.leralix.tan.listeners.chat.events.ChangeTerritoryDescription;
import org.leralix.tan.listeners.chat.events.ChangeTerritoryName;
import org.leralix.tan.storage.stored.FortStorage;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.List;

public abstract class SettingsMenus extends BasicGui {

    protected final TerritoryData territoryData;

    protected SettingsMenus(Player player, Lang title, TerritoryData territoryData, int nbRows) {
        super(player, title, nbRows);
        this.territoryData = territoryData;
    }

    protected GuiItem getTerritoryInfo() {

        LangType langType = tanPlayer.getLang();

        List<FilledLang> lore = TerritoryInfoLoreUtil.getTerritoryInfoLore(territoryData);
        lore.add(Lang.GUI_TOWN_INFO_CHANGE_ICON.get());
        lore.add(Lang.RIGHT_CLICK_TO_SELECT_MEMBER_HEAD.get());

        return IconManager.getInstance().get(IconKey.TERRITORY_ICON)
                .setName(Lang.GUI_TOWN_NAME.get(langType, territoryData.getName()))
                .setDescription(lore)
                .asGuiItem(player, langType);
    }

    protected GuiItem getRenameButton() {

        int cost = Constants.getChangeTerritoryNameCost(territoryData);

        return iconManager.get(IconKey.TERRITORY_RENAME_ICON)
                .setName(Lang.GUI_SETTINGS_CHANGE_TERRITORY_NAME.get(tanPlayer))
                .setDescription(
                        Lang.GUI_SETTINGS_CHANGE_TERRITORY_NAME_DESC1.get(territoryData.getName()),
                        Lang.GUI_SETTINGS_CHANGE_TERRITORY_NAME_DESC3.get(Integer.toString(cost))
                )
                .setRequirements(new RankPermissionRequirement(territoryData, tanPlayer, RolePermission.TOWN_ADMINISTRATOR))
                .setClickToAcceptMessage(Lang.GUI_GENERIC_CLICK_TO_MODIFY)
                .setAction(action -> {
                    TanChatUtils.message(player, Lang.ENTER_NEW_VALUE.get(tanPlayer));
                    PlayerChatListenerStorage.register(player, new ChangeTerritoryName(territoryData, cost, p -> open()));
                })
                .asGuiItem(player, langType);
    }

    protected GuiItem getChangeDescriptionButton() {
        return iconManager.get(IconKey.TERRITORY_DESCRIPTION_ICON)
                .setName(Lang.GUI_SETTINGS_CHANGE_TOWN_MESSAGE.get(tanPlayer))
                .setDescription(Lang.GUI_SETTINGS_CHANGE_TOWN_MESSAGE_DESC1.get(territoryData.getDescription()))
                .setRequirements(new RankPermissionRequirement(territoryData, tanPlayer, RolePermission.TOWN_ADMINISTRATOR))
                .setClickToAcceptMessage(Lang.GUI_GENERIC_CLICK_TO_MODIFY)
                .setAction(action -> {
                    TanChatUtils.message(player, Lang.ENTER_NEW_VALUE.get(tanPlayer));
                    PlayerChatListenerStorage.register(player, new ChangeTerritoryDescription(territoryData, p -> open()));
                })
                .asGuiItem(player, langType);
    }

    protected GuiItem getChangeColorButton() {
        return iconManager.get(IconKey.TERRITORY_CHANGE_COLOR_ICON)
                .setName(Lang.GUI_TOWN_SETTINGS_CHANGE_CHUNK_COLOR.get(tanPlayer))
                .setDescription(
                        Lang.GUI_TOWN_SETTINGS_CHANGE_CHUNK_COLOR_DESC1.get(),
                        Lang.GUI_TOWN_SETTINGS_CHANGE_CHUNK_COLOR_DESC2.get(territoryData.getChunkColor() + territoryData.getChunkColorInHex())
                )
                .setRequirements(new RankPermissionRequirement(territoryData, tanPlayer, RolePermission.TOWN_ADMINISTRATOR))
                .setClickToAcceptMessage(Lang.GUI_GENERIC_CLICK_TO_MODIFY)
                .setAction(action -> {
                    TanChatUtils.message(player, Lang.GUI_TOWN_SETTINGS_WRITE_NEW_COLOR_IN_CHAT.get(tanPlayer));
                    PlayerChatListenerStorage.register(player, new ChangeColor(territoryData, p -> open()));
                })
                .asGuiItem(player, langType);
    }

    protected GuiItem setBannerButton(){
        return iconManager.get(territoryData.getBanner().buildItemStack())
                .setName(Lang.GUI_TERRITORY_SETTINGS_SET_BANNER.get(tanPlayer))
                .setRequirements(
                        new RankPermissionRequirement(territoryData, tanPlayer, RolePermission.TOWN_ADMINISTRATOR)
                )
                .setClickToAcceptMessage(
                        Lang.GUI_SETTINGS_SET_FLAG_ACTION
                )
                .setAction(
                    action -> {

                        if(action.getCursor() == null){
                            return;
                        }
                        ItemStack itemMaterial = action.getCursor();

                        if(Tag.BANNERS.isTagged(itemMaterial.getType())) {
                            BannerMeta meta = (BannerMeta) itemMaterial.getItemMeta();
                            territoryData.setBanner(itemMaterial.getType(), meta.getPatterns());
                            FortStorage.getInstance().getOwnedFort(territoryData).forEach(Fort::updateFlag);
                            SoundUtil.playSound(player, SoundEnum.MINOR_GOOD);
                            open();
                        }
                        SoundUtil.playSound(player, SoundEnum.NOT_ALLOWED);
                    }
                )
                .asGuiItem(player, langType);
    }
}
