package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.gui.user.war.PlannedAttackMenu;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlannedAttackStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.gui.AsyncGuiHelper;
import org.leralix.tan.wars.PlannedAttack;

public class AttackMenu extends IteratorGUI {

  private final TerritoryData territoryData;
  private List<GuiItem> cachedAttacks = new ArrayList<>();
  private boolean isLoaded = false;

  private AttackMenu(Player player, ITanPlayer tanPlayer, TerritoryData territoryData) {
    super(player, tanPlayer, Lang.HEADER_WARS_MENU.get(tanPlayer.getLang()), 6);
    this.territoryData = territoryData;
  }

  public static void open(Player player, TerritoryData territoryData) {
    PlayerDataStorage.getInstance()
        .get(player)
        .thenAccept(
            tanPlayer -> {
              new AttackMenu(player, tanPlayer, territoryData).open();
            });
  }

  @Override
  public void open() {
    // Show immediate loading screen with cached data
    iterator(cachedAttacks, territoryData::openMainMenu);
    gui.open(player);

    // Load data asynchronously if not already loaded
    if (!isLoaded) {
      AsyncGuiHelper.loadAsync(
          player,
          () -> getWars(tanPlayer), // Async supplier - loads attack data
          items -> { // Main thread consumer - updates GUI
            cachedAttacks = items;
            isLoaded = true;
            iterator(items, territoryData::openMainMenu);
            gui.update();
          });
    }
  }

  private List<GuiItem> getWars(ITanPlayer tanPlayer) {
    ArrayList<GuiItem> guiItems = new ArrayList<>();
    for (PlannedAttack plannedAttack : PlannedAttackStorage.getInstance().getAllSync().values()) {
      ItemStack attackIcon = plannedAttack.getIcon(tanPlayer, territoryData);
      GuiItem attackButton =
          ItemBuilder.from(attackIcon)
              .asGuiItem(
                  event -> {
                    event.setCancelled(true);
                    PlannedAttackMenu.open(player, territoryData, plannedAttack);
                  });
      guiItems.add(attackButton);
    }
    return guiItems;
  }
}
