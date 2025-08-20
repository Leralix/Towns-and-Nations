package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.newhistory.TransactionHistory;
import org.leralix.tan.dataclass.newhistory.TransactionHistoryEnum;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.utils.deprecated.HeadUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EconomicHistoryMenu extends IteratorGUI {


    private final TerritoryData territoryData;
    private final TransactionHistoryEnum transactionHistoryEnum;

    public EconomicHistoryMenu(Player player, TerritoryData territoryData, TransactionHistoryEnum transactionHistoryEnum) {
        super(player, Lang.HEADER_HISTORY.get(player), 6);
        this.territoryData = territoryData;
        this.transactionHistoryEnum = transactionHistoryEnum;
        open();
    }

    @Override
    public void open() {

        iterator(getEconomicsHistory(), p -> new TreasuryMenu(player, territoryData));

        gui.open(player);
    }

    private List<GuiItem> getEconomicsHistory() {
        List<GuiItem> guiItems = new ArrayList<>();

        for (List<TransactionHistory> transactionHistory : TownsAndNations.getPlugin().getDatabaseHandler().getTransactionHistory(territoryData, transactionHistoryEnum)) {
            ItemStack transactionIcon = HeadUtils.createCustomItemStack(Material.PAPER, ChatColor.GREEN + transactionHistory.get(0).getDate());

            for (TransactionHistory transaction : transactionHistory) {
                HeadUtils.addLore(transactionIcon, transaction.addLoreLine());
            }
            guiItems.add(ItemBuilder.from(transactionIcon).asGuiItem(event -> event.setCancelled(true)));
        }

        Collections.reverse(guiItems); //newer first
        return guiItems;
    }
}
