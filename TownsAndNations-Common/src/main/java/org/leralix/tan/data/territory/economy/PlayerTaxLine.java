package org.leralix.tan.data.territory.economy;

import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.TownData;
import org.leralix.tan.data.territory.rank.RolePermission;
import org.leralix.tan.economy.EconomyUtil;
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
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.text.StringUtil;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.UUID;

public class PlayerTaxLine extends ProfitLine {

    double actualTaxes = 0;
    double missingTaxes = 0;

    public PlayerTaxLine(TownData townData) {
        super(townData);
        double flatTax = townData.getTax();
        for (String playerID : townData.getPlayerIDList()) {
            ITanPlayer othertanPlayer = PlayerDataStorage.getInstance().get(playerID);
            OfflinePlayer otherPlayer = Bukkit.getOfflinePlayer(UUID.fromString(playerID));
            if (!othertanPlayer.getTownRank().isPayingTaxes()) {
                continue;
            }
            if (EconomyUtil.getBalance(otherPlayer) < flatTax)
                missingTaxes += flatTax;
            else
                actualTaxes += flatTax;
        }

    }

    @Override
    protected double getMoney() {
        return actualTaxes;
    }

    @Override
    public FilledLang getLine() {
        if (missingTaxes > 0)
            return Lang.PLAYER_TAX_MISSING_LINE.get(StringUtil.getColoredMoney(getMoney()), StringUtil.formatMoney(missingTaxes));
        else
            return Lang.PLAYER_TAX_LINE.get(StringUtil.getColoredMoney(getMoney()));
    }

    @Override
    public void addItems(Gui gui, Player player, LangType lang) {

        ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(player);


        gui.setItem(2, 2, getDecreaseButton(player, lang, tanPlayer));
        gui.setItem(2, 3, getInfoIcon(player, lang, tanPlayer));
        gui.setItem(2, 4, getIncreaseButton(player, lang, tanPlayer));
    }

    private GuiItem getIncreaseButton(Player player, LangType lang, ITanPlayer tanPlayer) {
        return IconManager.getInstance().get(IconKey.TAX_INCREASE_ICON)
                .setName(Lang.GUI_TREASURY_INCREASE_TAX.get(lang))
                .setDescription(
                        Lang.GUI_INCREASE_1_DESC.get(),
                        Lang.GUI_INCREASE_10_DESC.get()
                )
                .setAction(action -> {
                    action.setCancelled(true);

                    if (!territoryData.doesPlayerHavePermission(tanPlayer, RolePermission.MANAGE_TAXES)) {
                        TanChatUtils.message(player, Lang.PLAYER_NO_PERMISSION.get(lang));
                        return;
                    }

                    int amountToAdd = action.isShiftClick() ? 10 : 1;

                    territoryData.addToTax(amountToAdd);
                    SoundUtil.playSound(player, SoundEnum.ADD);
                    new TreasuryMenu(player, territoryData);
                })
                .asGuiItem(player, lang);
    }

    private GuiItem getInfoIcon(Player player, LangType lang, ITanPlayer tanPlayer) {
        return IconManager.getInstance().get(IconKey.TAX_INFO_ICON)
                .setName(Lang.GUI_TREASURY_FLAT_TAX.get(lang))
                .setDescription(
                        Lang.GUI_TREASURY_FLAT_TAX_DESC1.get(StringUtil.formatMoney(territoryData.getTax())),
                        Lang.GUI_GENERIC_CLICK_TO_OPEN_HISTORY.get(),
                        Lang.RIGHT_CLICK_TO_SET_TAX.get()
                )
                .setAction(action -> {
                    action.setCancelled(true);
                    if (!territoryData.doesPlayerHavePermission(tanPlayer, RolePermission.MANAGE_TAXES)) {
                        TanChatUtils.message(player, Lang.PLAYER_NO_PERMISSION.get(lang));
                        return;
                    }
                    if (action.isLeftClick()) {
                        new TerritoryTransactionHistory(player, territoryData, TransactionType.TAXES, p -> new TreasuryMenu(player, territoryData));
                    } else if (action.isRightClick()) {
                        TanChatUtils.message(player, Lang.TOWN_SET_TAX_IN_CHAT.get(lang));
                        PlayerChatListenerStorage.register(player, new SetTerritoryTax(territoryData, p -> new TreasuryMenu(player, territoryData)));
                    }
                })
                .asGuiItem(player, lang);
    }

    private GuiItem getDecreaseButton(Player player, LangType lang, ITanPlayer tanPlayer) {
        return IconManager.getInstance().get(IconKey.TAX_DECREASE_ICON)
                .setName(Lang.GUI_TREASURY_LOWER_TAX.get(lang))
                .setDescription(
                        Lang.GUI_DECREASE_1_DESC.get(),
                        Lang.GUI_DECREASE_10_DESC.get()
                )
                .setAction(action -> {
                    action.setCancelled(true);
                    if (!territoryData.doesPlayerHavePermission(tanPlayer, RolePermission.MANAGE_TAXES)) {
                        TanChatUtils.message(player, Lang.PLAYER_NO_PERMISSION.get(lang));
                        return;
                    }

                    double currentTax = territoryData.getTax();
                    int amountToRemove = action.isShiftClick() && currentTax > 9 ? 10 : 1;

                    if (currentTax <= 0) {
                        TanChatUtils.message(player, Lang.GUI_TREASURY_CANT_TAX_LESS.get(lang));
                        return;
                    }
                    SoundUtil.playSound(player, SoundEnum.REMOVE);

                    territoryData.addToTax(-amountToRemove);
                    new TreasuryMenu(player, territoryData);
                })
                .asGuiItem(player, lang);
    }

    @Override
    public boolean isRecurrent() {
        return true;
    }
}
