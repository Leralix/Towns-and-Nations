package org.leralix.tan.gui.admin;

import dev.triumphteam.gui.guis.GuiItem;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.gui.user.war.WarMenu;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.WarStorage;
import org.leralix.tan.utils.deprecated.GuiUtil;
import org.leralix.tan.utils.gui.AsyncGuiHelper;
import org.leralix.tan.wars.War;

public class AdminWarsMenu extends IteratorGUI {

  private List<GuiItem> cachedWars = new ArrayList<>();
  private boolean isLoaded = false;

  private AdminWarsMenu(Player player, ITanPlayer tanPlayer) {
    super(player, tanPlayer, "Admin - Wars List", 6);
  }

  public static void open(Player player) {
    PlayerDataStorage.getInstance()
        .get(player)
        .thenAccept(
            tanPlayer -> {
              new AdminWarsMenu(player, tanPlayer).open();
            });
  }

  @Override
  public void open() {
    // Show immediate loading screen with cached data
    GuiUtil.createIterator(
        gui,
        cachedWars,
        page,
        player,
        p -> AdminMainMenu.open(player),
        p -> nextPage(),
        p -> previousPage());
    gui.open(player);

    // Load data asynchronously if not already loaded
    if (!isLoaded) {
      AsyncGuiHelper.loadAsync(
          player,
          this::getAllWars, // Async supplier - loads war data
          items -> { // Main thread consumer - updates GUI
            cachedWars = items;
            isLoaded = true;
            GuiUtil.createIterator(
                gui,
                items,
                page,
                player,
                p -> AdminMainMenu.open(player),
                p -> nextPage(),
                p -> previousPage());
            gui.update();
          });
    }
  }

  private List<GuiItem> getAllWars() {
    List<War> wars = new ArrayList<>(WarStorage.getInstance().getAllSync().values());

    List<GuiItem> guiItems = new ArrayList<>();
    for (War war : wars) {
      guiItems.add(
          iconManager
              .get(war.getIcon())
              .setName(war.getName())
              .setDescription(
                  Lang.ATTACK_ICON_DESC_1.get(war.getMainAttacker().getColoredName()),
                  Lang.ATTACK_ICON_DESC_2.get(war.getMainDefender().getColoredName()))
              .setAction(event -> WarMenu.open(player, null, war))
              .asGuiItem(player, langType));
    }

    return guiItems;
  }
}
