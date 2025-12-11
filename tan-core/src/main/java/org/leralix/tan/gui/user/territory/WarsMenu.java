package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.guis.GuiItem;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.gui.user.war.WarMenu;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.WarStorage;
import org.leralix.tan.utils.gui.AsyncGuiHelper;
import org.leralix.tan.wars.War;

public class WarsMenu extends IteratorGUI {

  private final TerritoryData territoryData;
  private List<GuiItem> cachedWars = new ArrayList<>();
  private boolean isLoaded = false;

  private WarsMenu(Player player, ITanPlayer tanPlayer, TerritoryData territoryData) {
    super(player, tanPlayer, "War Menu", 4);
    this.territoryData = territoryData;
  }

  public static void open(Player player, TerritoryData territoryData) {
    PlayerDataStorage.getInstance()
        .get(player)
        .thenAccept(
            tanPlayer -> {
              new WarsMenu(player, tanPlayer, territoryData).open();
            });
  }

  @Override
  public void open() {
    iterator(cachedWars, p -> territoryData.openMainMenu(player));
    gui.setItem(4, 4, getAttackButton(territoryData));
    gui.open(player);

    if (!isLoaded) {
      AsyncGuiHelper.loadAsync(
          player,
          () -> getWars(territoryData),
          items -> {
            cachedWars = items;
            isLoaded = true;
            iterator(items, p -> territoryData.openMainMenu(player));
            gui.setItem(4, 4, getAttackButton(territoryData));
            gui.update();
          });
    }
  }

  private List<GuiItem> getWars(TerritoryData territoryData) {

    List<War> wars = WarStorage.getInstance().getWarsOfTerritory(territoryData);

    List<GuiItem> guiItems = new ArrayList<>();
    for (War war : wars) {
      guiItems.add(
          iconManager
              .get(war.getIcon())
              .setName(war.getName())
              .setDescription(
                  Lang.ATTACK_ICON_DESC_1.get(war.getMainAttacker().getColoredName()),
                  Lang.ATTACK_ICON_DESC_2.get(war.getMainDefender().getColoredName()))
              .setAction(event -> WarMenu.open(player, territoryData, war))
              .asGuiItem(player, langType));
    }

    return guiItems;
  }

  private @NotNull GuiItem getAttackButton(TerritoryData territoryData) {
    return iconManager
        .get(new ItemStack(Material.BOW))
        .setName("Open Attacks")
        .setAction(p -> AttackMenu.open(player, territoryData))
        .asGuiItem(player, langType);
  }
}
