package org.leralix.tan.dataclass.territory.economy;

import org.leralix.lib.gui
.builder.item.ItemBuilder;
import org.leralix.lib.gui
.guis.Gui;
import org.leralix.lib.gui
.guis.GuiItem;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.dataclass.PlayerData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.economy.EconomyUtil;
import org.leralix.tan.enums.RolePermission;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.TanChatUtils;
import org.leralix.tan.utils.HeadUtils;
import org.leralix.tan.utils.StringUtil;
import org.leralix.tan.dataclass.newhistory.TransactionHistoryEnum;
import org.leralix.tan.gui.PlayerGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.chat.PlayerChatListenerStorage;
import org.leralix.tan.listeners.chat.events.SetSpecificTax;

import java.util.UUID;

public class PlayerTaxLine extends ProfitLine{

    double actualTaxes = 0;
    double missingTaxes = 0;

    public PlayerTaxLine(TownData townData){
        super(townData);
        double flatTax = townData.getTax();
        for (String playerID : townData.getPlayerIDList()){
            PlayerData otherPlayerData = PlayerDataStorage.get(playerID);
            OfflinePlayer otherPlayer = Bukkit.getOfflinePlayer(UUID.fromString(playerID));
            if(!otherPlayerData.getTownRank().isPayingTaxes()){
                continue;
            }
            if(EconomyUtil.getBalance(otherPlayer) < flatTax)
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
    public String getLine() {
       if(missingTaxes > 0)
           return Lang.PLAYER_TAX_MISSING_LINE.get(StringUtil.getColoredMoney(getMoney()), StringUtil.formatMoney(missingTaxes));
       else
           return Lang.PLAYER_TAX_LINE.get(StringUtil.getColoredMoney(getMoney()));
    }

    @Override
    public void addItems(Gui gui, Player player) {

        PlayerData playerData = PlayerDataStorage.get(player);

        ItemStack lowerTax = HeadUtils.makeSkullB64(Lang.GUI_TREASURY_LOWER_TAX.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGU0YjhiOGQyMzYyYzg2NGUwNjIzMDE0ODdkOTRkMzI3MmE2YjU3MGFmYmY4MGMyYzViMTQ4Yzk1NDU3OWQ0NiJ9fX0=",
                Lang.GUI_DECREASE_1_DESC.get(),
                Lang.GUI_DECREASE_10_DESC.get());
        ItemStack increaseTax = HeadUtils.makeSkullB64(Lang.GUI_TREASURY_INCREASE_TAX.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWZmMzE0MzFkNjQ1ODdmZjZlZjk4YzA2NzU4MTA2ODFmOGMxM2JmOTZmNTFkOWNiMDdlZDc4NTJiMmZmZDEifX19",
                Lang.GUI_INCREASE_1_DESC.get(),
                Lang.GUI_INCREASE_10_DESC.get());
        ItemStack tax = HeadUtils.makeSkullB64(Lang.GUI_TREASURY_FLAT_TAX.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTk4ZGY0MmY0NzdmMjEzZmY1ZTlkN2ZhNWE0Y2M0YTY5ZjIwZDljZWYyYjkwYzRhZTRmMjliZDE3Mjg3YjUifX19",
                Lang.GUI_TREASURY_FLAT_TAX_DESC1.get(StringUtil.formatMoney(territoryData.getTax())),
                Lang.GUI_GENERIC_CLICK_TO_OPEN_HISTORY.get(),
                Lang.RIGHT_CLICK_TO_SET_TAX.get());


        GuiItem lowerTaxButton = ItemBuilder.from(lowerTax).asGuiItem(event -> {
            event.setCancelled(true);
            if(!territoryData.doesPlayerHavePermission(playerData, RolePermission.MANAGE_TAXES)) {
                player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION.get());
                return;
            }

            double currentTax = territoryData.getTax();
            int amountToRemove = event.isShiftClick() && currentTax > 9 ? 10 : 1;

            if(currentTax <= 0){
                player.sendMessage(TanChatUtils.getTANString() + Lang.GUI_TREASURY_CANT_TAX_LESS.get());
                return;
            }
            SoundUtil.playSound(player, SoundEnum.REMOVE);

            territoryData.addToTax(-amountToRemove);
            PlayerGUI.openTreasury(player, territoryData);
        });
        GuiItem taxInfo = ItemBuilder.from(tax).asGuiItem(event -> {
            event.setCancelled(true);
            if(event.isLeftClick()){
                PlayerGUI.openTownEconomicsHistory(player, territoryData, TransactionHistoryEnum.PLAYER_TAX);
            }
            else if(event.isRightClick()){
                player.sendMessage(TanChatUtils.getTANString() + Lang.TOWN_SET_TAX_IN_CHAT.get());
                PlayerChatListenerStorage.register(player, new SetSpecificTax(territoryData));
                player.closeInventory();
            }
        });
        GuiItem increaseTaxButton = ItemBuilder.from(increaseTax).asGuiItem(event -> {
            event.setCancelled(true);

            if(!territoryData.doesPlayerHavePermission(playerData, RolePermission.MANAGE_TAXES)){
                player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_NO_PERMISSION.get());
                return;
            }

            int amountToAdd = event.isShiftClick() ? 10 : 1;

            territoryData.addToTax(amountToAdd);
            SoundUtil.playSound(player, SoundEnum.ADD);
            PlayerGUI.openTreasury(player, territoryData);
        });

        gui.setItem(2, 2, lowerTaxButton);
        gui.setItem(2, 3, taxInfo);
        gui.setItem(2, 4, increaseTaxButton);
    }

    @Override
    public boolean isRecurrent() {
        return true;
    }
}
