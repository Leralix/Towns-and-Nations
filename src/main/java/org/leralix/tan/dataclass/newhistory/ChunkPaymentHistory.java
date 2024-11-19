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

    public class ChunkPaymentHistory extends TransactionHistory {

    public ChunkPaymentHistory(String date, String territoryID, double amount) {
        super(date, territoryID, null, amount);
    }
    public ChunkPaymentHistory(ITerritoryData territoryData, double amount) {
        super(territoryData.getID(), null, amount);
    }


    @Override
    public TransactionHistoryEnum getType() {
        return TransactionHistoryEnum.CHUNK_SPENDING;
    }

    @Override
    public GuiItem createGuiItem() {

        ItemStack item = HeadUtils.createCustomItemStack(Material.PAPER, getDate(), String.valueOf(getAmount()));
        return ItemBuilder.from(item).asGuiItem(e -> e.setCancelled(true));
    }
}
