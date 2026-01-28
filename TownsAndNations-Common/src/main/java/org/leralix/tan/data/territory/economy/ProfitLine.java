package org.leralix.tan.data.territory.economy;

import dev.triumphteam.gui.guis.Gui;
import org.bukkit.entity.Player;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.data.territory.TerritoryData;
import org.leralix.tan.data.territory.rank.RolePermission;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.gui.user.territory.TreasuryMenu;
import org.leralix.tan.gui.user.territory.history.TerritoryTransactionHistory;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.listeners.chat.PlayerChatListenerStorage;
import org.leralix.tan.listeners.chat.events.treasury.SetTerritoryTax;
import org.leralix.tan.storage.database.transactions.TransactionType;
import org.leralix.tan.utils.text.TanChatUtils;

public abstract class ProfitLine implements Comparable<ProfitLine> {

    protected final TerritoryData territoryData;

    protected ProfitLine(TerritoryData territoryData) {
        this.territoryData = territoryData;
    }

    protected abstract double getMoney();

    public abstract FilledLang getLine();

    public abstract void addItems(Gui gui, Player player, LangType langType);

    @Override
    public int compareTo(ProfitLine otherLine) {
        return Double.compare(otherLine.getMoney(), getMoney());
    }

    public abstract boolean isRecurrent();

    protected void addDecreaseItem(Gui gui, Player player, LangType lang, double taxRate) {
        gui.setItem(2, 2, IconManager.getInstance().get(IconKey.TAX_DECREASE_ICON)
                .setName(Lang.GUI_TREASURY_LOWER_TAX.get(lang))
                .setDescription(
                        Lang.GUI_DECREASE_1_DESC.get(),
                        Lang.GUI_DECREASE_10_DESC.get()
                )
                .setAction(action -> {
                    if (!territoryData.doesPlayerHavePermission(player, RolePermission.MANAGE_TAXES)) {
                        TanChatUtils.message(player, Lang.PLAYER_NO_PERMISSION.get(lang));
                        return;
                    }
                    int amountToRemove = action.isShiftClick() && taxRate > 10 ? 10 : 1;
                    if (taxRate < 1) {
                        TanChatUtils.message(player, Lang.GUI_TREASURY_CANT_TAX_LESS.get(lang));
                        return;
                    }
                    SoundUtil.playSound(player, SoundEnum.REMOVE);
                    territoryData.addToTax(-amountToRemove);
                    new TreasuryMenu(player, territoryData);
                })
                .asGuiItem(player, lang)
        );
    }

    protected void addInfoItem(Gui gui, Player player, LangType lang, double taxRate) {
        gui.setItem(2, 3, IconManager.getInstance().get(IconKey.TAX_INFO_ICON)
                .setName(Lang.GUI_TREASURY_FLAT_TAX.get(lang))
                .setDescription(
                        Lang.GUI_TREASURY_FLAT_TAX_DESC1.get(Double.toString(taxRate)),
                        Lang.GUI_GENERIC_CLICK_TO_OPEN_HISTORY.get(),
                        Lang.RIGHT_CLICK_TO_SET_TAX.get()
                )
                .setAction(action -> {
                    if (!territoryData.doesPlayerHavePermission(player, RolePermission.MANAGE_TAXES)) {
                        TanChatUtils.message(player, Lang.PLAYER_NO_PERMISSION.get(lang));
                        return;
                    }
                    action.setCancelled(true);
                    if (action.isLeftClick()) {
                        new TerritoryTransactionHistory(player, territoryData, TransactionType.TERRITORY_TAX, p -> new TreasuryMenu(player, territoryData));
                    } else if (action.isRightClick()) {
                        TanChatUtils.message(player, Lang.TOWN_SET_TAX_IN_CHAT.get(lang));
                        PlayerChatListenerStorage.register(player, new SetTerritoryTax(territoryData, p -> new TreasuryMenu(player, territoryData)));
                    }
                })
                .asGuiItem(player, lang)
        );
    }

    protected void addIncreaseItem(Gui gui, Player player, LangType lang) {
        gui.setItem(2, 4, IconManager.getInstance().get(IconKey.TAX_INCREASE_ICON)
                .setName(Lang.GUI_TREASURY_INCREASE_TAX.get(lang))
                .setDescription(
                        Lang.GUI_INCREASE_1_DESC.get(),
                        Lang.GUI_INCREASE_10_DESC.get()
                )
                .setAction(action -> {
                    if (!territoryData.doesPlayerHavePermission(player, RolePermission.MANAGE_TAXES)) {
                        TanChatUtils.message(player, Lang.PLAYER_NO_PERMISSION.get(lang));
                        return;
                    }
                    int amountToAdd = action.isShiftClick() ? 10 : 1;
                    SoundUtil.playSound(player, SoundEnum.ADD);
                    territoryData.addToTax(amountToAdd);
                    new TreasuryMenu(player, territoryData);
                })
                .asGuiItem(player, lang)
        );
    }
}
