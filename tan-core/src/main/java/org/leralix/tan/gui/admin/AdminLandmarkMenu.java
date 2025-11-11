package org.leralix.tan.gui.admin;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.Landmark;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.gui.landmark.LandmarkNoOwnerMenu;
import org.leralix.tan.storage.stored.LandmarkStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.deprecated.GuiUtil;
import org.leralix.tan.utils.gui.AsyncGuiHelper;

public class AdminLandmarkMenu extends IteratorGUI {

  private List<GuiItem> cachedLandmarks = new ArrayList<>();
  private boolean isLoaded = false;

  private AdminLandmarkMenu(Player player, ITanPlayer tanPlayer) {
    super(player, tanPlayer, "Admin - Landmarks List", 6);
  }

  public static void open(Player player) {
    PlayerDataStorage.getInstance()
        .get(player)
        .thenAccept(
            tanPlayer -> {
              new AdminLandmarkMenu(player, tanPlayer).open();
            });
  }

  @Override
  public void open() {
    // Show immediate loading screen with cached data
    GuiUtil.createIterator(
        gui,
        cachedLandmarks,
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
          this::getAllLandmarks, // Async supplier - loads landmark data
          items -> { // Main thread consumer - updates GUI
            cachedLandmarks = items;
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

  private List<GuiItem> getAllLandmarks() {
    List<Landmark> landmarks = new ArrayList<>(LandmarkStorage.getInstance().getAllSync().values());

    List<GuiItem> guiItems = new ArrayList<>();
    for (Landmark landmark : landmarks) {
      String ownerName = "No Owner";
      if (landmark.isOwned() && landmark.getOwnerID() != null) {
        TownData owner = TownDataStorage.getInstance().getSync(landmark.getOwnerID());
        if (owner != null) {
          ownerName = owner.getColoredName();
        }
      }

      final String finalOwnerName = ownerName;
      guiItems.add(
          ItemBuilder.from(Material.BEACON)
              .name(Component.text(landmark.getName()))
              .lore(
                  Component.text("Owner: " + finalOwnerName),
                  Component.text(
                      "Location: "
                          + landmark.getPosition().getX()
                          + ", "
                          + landmark.getPosition().getY()
                          + ", "
                          + landmark.getPosition().getZ()))
              .asGuiItem(
                  event -> {
                    if (landmark.isOwned() && landmark.getOwnerID() != null) {
                      TownData owner = TownDataStorage.getInstance().getSync(landmark.getOwnerID());
                      if (owner != null) {
                        owner.openMainMenu(player);
                      }
                    } else {
                      LandmarkNoOwnerMenu.open(player, landmark);
                    }
                  }));
    }

    return guiItems;
  }
}
