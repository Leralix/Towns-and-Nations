package org.leralix.tan.dataclass.newhistory;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.leralix.tan.dataclass.territory.ITerritoryData;
import org.leralix.tan.utils.HeadUtils;
import org.leralix.tan.utils.TerritoryUtil;

import java.util.UUID;

public class SubjectTaxHistory extends TransactionHistory {


    public SubjectTaxHistory(String date, String territoryDataID, String playerID, double amount) {
        super(date, territoryDataID, playerID, amount);
    }
    public SubjectTaxHistory(ITerritoryData territoryData, ITerritoryData subject, double amount) {
        super(territoryData.getID(),subject.getID(),amount);
    }

    @Override
    public TransactionHistoryEnum getType() {
        return TransactionHistoryEnum.SUBJECT_TAX;
    }

    @Override
    public GuiItem createGuiItem() {
        ITerritoryData territoryData = TerritoryUtil.getTerritory(getTerritoryDataID());
        String playerName = territoryData != null ? territoryData.getColoredName() : "Unknown";
        ItemStack item = HeadUtils.createCustomItemStack(Material.PAPER, getDate(), playerName, String.valueOf(getAmount()));
        return ItemBuilder.from(item).asGuiItem(e -> e.setCancelled(true));
    }
}
