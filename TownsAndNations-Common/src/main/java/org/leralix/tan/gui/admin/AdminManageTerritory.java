package org.leralix.tan.gui.admin;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.leralix.lib.data.SoundEnum;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.user.territory.history.TerritoryTransactionHistory;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.chat.PlayerChatListenerStorage;
import org.leralix.tan.listeners.chat.events.ChangeTerritoryDescription;
import org.leralix.tan.listeners.chat.events.ChangeTerritoryName;
import org.leralix.tan.storage.stored.WarStorage;
import org.leralix.tan.utils.file.FileUtil;
import org.leralix.tan.utils.text.TanChatUtils;

public abstract class AdminManageTerritory extends BasicGui {

    protected final TerritoryData territoryData;

    protected AdminManageTerritory(Player player, FilledLang menuName, int nbRows, TerritoryData territoryData) {
        super(player, menuName, nbRows);
        this.territoryData = territoryData;
    }


    protected GuiItem getRenameTerritory(){
        return iconManager.get(IconKey.TERRITORY_RENAME_ICON)
                .setName(Lang.GUI_SETTINGS_CHANGE_TERRITORY_NAME.get(tanPlayer))
                .setDescription(Lang.GUI_SETTINGS_CHANGE_TERRITORY_NAME_DESC1.get(territoryData.getName()))
                .setClickToAcceptMessage(Lang.GUI_GENERIC_CLICK_TO_MODIFY)
                .setAction(action -> {
                    TanChatUtils.message(player, Lang.ENTER_NEW_VALUE.get(player));
                    PlayerChatListenerStorage.register(player, new ChangeTerritoryName(territoryData, 0, p -> open()));
                })
                .asGuiItem(player, langType);
    }

    protected GuiItem getChangeDescription(){
        return iconManager.get(IconKey.TERRITORY_DESCRIPTION_ICON)
                .setName(Lang.GUI_SETTINGS_CHANGE_TOWN_MESSAGE.get(tanPlayer))
                .setDescription(Lang.GUI_SETTINGS_CHANGE_TOWN_MESSAGE_DESC1.get(territoryData.getDescription()))
                .setClickToAcceptMessage(Lang.GUI_GENERIC_CLICK_TO_MODIFY)
                .setAction(action -> {
                    TanChatUtils.message(player, Lang.ENTER_NEW_VALUE.get(tanPlayer));
                    PlayerChatListenerStorage.register(player, new ChangeTerritoryDescription(territoryData, p -> open()));
                })
                .asGuiItem(player, langType);
    }

    protected GuiItem getDelete(){
       return iconManager.get(IconKey.TOWN_DELETE_TOWN_ICON)
               .setName(Lang.ADMIN_GUI_DELETE_TERRITORY.get(langType))
               .setDescription(Lang.ADMIN_GUI_DELETE_TERRITORY_DESC1.get(territoryData.getColoredName()))
               .setAction(action -> {
                   FileUtil.addLineToHistory(Lang.REGION_DELETED_NEWSLETTER.get(player.getName(), territoryData.getName()));
                   if (territoryData.isCapital()) {
                       territoryData.getOverlord().ifPresent(overlord ->
                               TanChatUtils.message(player, Lang.CANNOT_DELETE_TERRITORY_IF_CAPITAL.get(langType, overlord.getBaseColoredName()), SoundEnum.NOT_ALLOWED)
                       );
                       return;
                   }

                   // A territory cannot be deleted if it is at war to avoid errors
                   if(!WarStorage.getInstance().getWarsOfTerritory(territoryData).isEmpty()){
                        TanChatUtils.message(player, Lang.CANNOT_DELETE_TERRITORY_IF_AT_WAR.get(langType), SoundEnum.NOT_ALLOWED);
                       return;
                   }

                   territoryData.delete();
                   player.closeInventory();
               })
               .asGuiItem(player,langType);
    }

    protected GuiItem getTransactionHistory(){
        return iconManager.get(IconKey.BUDGET_ICON)
                .setName(Lang.ADMIN_GET_TRANSACTION_HISTORY.get(langType))
                .setClickToAcceptMessage(Lang.GUI_GENERIC_CLICK_TO_OPEN)
                .setAction(action -> new TerritoryTransactionHistory(player, territoryData, p -> open()))
                .asGuiItem(player, langType);
    }

}
