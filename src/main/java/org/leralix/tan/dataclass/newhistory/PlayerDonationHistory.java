package org.leralix.tan.dataclass.newhistory;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.tan.dataclass.PlayerData;
import org.leralix.tan.dataclass.territory.ITerritoryData;
import org.leralix.tan.utils.HeadUtils;

import java.util.UUID;

public class PlayerDonationHistory extends TransactionHistory {


    public PlayerDonationHistory(String date, String territoryDataID, String playerID, double amount) {
        super(date, territoryDataID, playerID, amount);
    }
    public PlayerDonationHistory(ITerritoryData territoryData, Player player, double amount) {
        super(territoryData.getID(),player.getUniqueId().toString(),amount);
    }


    @Override
    public TransactionHistoryEnum getType() {
        return TransactionHistoryEnum.DONATION;
    }

    @Override
    public GuiItem createGuiItem() {
        OfflinePlayer player = Bukkit.getPlayer(UUID.fromString(getTransactionParty()));
        String playerName = player != null ? player.getName() : "Unknown";
        ItemStack item = HeadUtils.createCustomItemStack(Material.PAPER, getDate(), playerName, String.valueOf(getAmount()));
        return ItemBuilder.from(item).asGuiItem(e -> e.setCancelled(true));
    }
}
