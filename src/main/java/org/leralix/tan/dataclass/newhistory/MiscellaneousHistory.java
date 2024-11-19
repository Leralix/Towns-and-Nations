package org.leralix.tan.dataclass.newhistory;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.leralix.tan.dataclass.territory.ITerritoryData;
import org.leralix.tan.utils.HeadUtils;

import java.util.UUID;

public class MiscellaneousHistory extends TransactionHistory {

    public MiscellaneousHistory(String date, String territoryDataID, double amount) {
        super(date, territoryDataID, null, amount);
    }

    public MiscellaneousHistory(ITerritoryData territoryData, double amount) {
        super(territoryData.getID(), null, amount);
    }

    @Override
    public TransactionHistoryEnum getType() {
        return TransactionHistoryEnum.MISCELLANEOUS;
    }

    @Override
    public GuiItem createGuiItem() {
        ItemStack item = HeadUtils.createCustomItemStack(Material.PAPER, getDate(), getTransactionParty(), String.valueOf(getAmount()));
        return ItemBuilder.from(item).asGuiItem(e -> e.setCancelled(true));
    }
}
