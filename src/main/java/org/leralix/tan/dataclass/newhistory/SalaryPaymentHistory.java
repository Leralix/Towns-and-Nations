package org.leralix.tan.dataclass.newhistory;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.leralix.tan.dataclass.RankData;
import org.leralix.tan.dataclass.territory.ITerritoryData;
import org.leralix.tan.utils.HeadUtils;
import org.leralix.tan.utils.TerritoryUtil;

import java.util.UUID;

public class SalaryPaymentHistory extends TransactionHistory {


    public SalaryPaymentHistory(String date, String territoryDataID, String rankID, double amount) {
        super(date, territoryDataID, rankID, amount);
    }

    public SalaryPaymentHistory(ITerritoryData territoryData, String rankID, double amount) {
        super(territoryData.getID(), rankID, amount);
    }

    @Override
    public TransactionHistoryEnum getType() {
        return TransactionHistoryEnum.SALARY;
    }

    @Override
    public GuiItem createGuiItem() {
        ITerritoryData territoryData = TerritoryUtil.getTerritory(getTerritoryDataID());
        RankData rankData = territoryData.getRank(Integer.valueOf(getTransactionParty()));
        ItemStack item = HeadUtils.createCustomItemStack(Material.PAPER, getDate(), rankData.getColoredName(), String.valueOf(getAmount()));
        return ItemBuilder.from(item).asGuiItem(e -> e.setCancelled(true));
    }
}
