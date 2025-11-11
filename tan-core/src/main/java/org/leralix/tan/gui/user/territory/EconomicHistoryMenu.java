package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.newhistory.TransactionHistory;
import org.leralix.tan.dataclass.newhistory.TransactionHistoryEnum;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.deprecated.HeadUtils;
import org.leralix.tan.utils.gui.AsyncGuiHelper;

public class EconomicHistoryMenu extends IteratorGUI {

  private final TerritoryData territoryData;
  private final TransactionHistoryEnum transactionHistoryEnum;
  private List<GuiItem> cachedHistory = new ArrayList<>();
  private boolean isLoaded = false;

  private EconomicHistoryMenu(
      Player player,
      ITanPlayer tanPlayer,
      TerritoryData territoryData,
      TransactionHistoryEnum transactionHistoryEnum) {
    super(player, tanPlayer, Lang.HEADER_HISTORY.get(tanPlayer.getLang()), 6);
    this.territoryData = territoryData;
    this.transactionHistoryEnum = transactionHistoryEnum;
  }

  public static void open(
      Player player, TerritoryData territoryData, TransactionHistoryEnum transactionHistoryEnum) {
    PlayerDataStorage.getInstance()
        .get(player)
        .thenAccept(
            tanPlayer -> {
              new EconomicHistoryMenu(player, tanPlayer, territoryData, transactionHistoryEnum)
                  .open();
            });
  }

  @Override
  public void open() {
    // Show immediate loading screen with cached data
    iterator(cachedHistory, p -> TreasuryMenu.open(player, territoryData));
    gui.open(player);

    // Load data asynchronously if not already loaded
    if (!isLoaded) {
      AsyncGuiHelper.loadAsync(
          player,
          () -> getEconomicsHistory(), // Async supplier - loads from database
          items -> { // Main thread consumer - updates GUI
            cachedHistory = items;
            isLoaded = true;
            iterator(items, p -> TreasuryMenu.open(player, territoryData));
            gui.update();
          });
    }
  }

  private List<GuiItem> getEconomicsHistory() {
    List<GuiItem> guiItems = new ArrayList<>();

    for (List<TransactionHistory> transactionHistory :
        TownsAndNations.getPlugin()
            .getDatabaseHandler()
            .getTransactionHistory(territoryData, transactionHistoryEnum)) {
      ItemStack transactionIcon =
          HeadUtils.createCustomItemStack(
              Material.PAPER, "§a" + transactionHistory.get(0).getDate());

      for (TransactionHistory transaction : transactionHistory) {
        HeadUtils.addLore(transactionIcon, transaction.addLoreLine());
      }
      guiItems.add(ItemBuilder.from(transactionIcon).asGuiItem(event -> event.setCancelled(true)));
    }

    Collections.reverse(guiItems); // newer first
    return guiItems;
  }
}
