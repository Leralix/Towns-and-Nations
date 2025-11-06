package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.guis.GuiItem;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.gui.user.war.WarMenu;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.WarStorage;
import org.leralix.tan.wars.War;

public class WarsMenu extends IteratorGUI {

  private final TerritoryData territoryData;

  public WarsMenu(Player player, TerritoryData territoryData) {
    super(player, "War Menu", 4);
    this.territoryData = territoryData;
    open();
  }

  @Override
  public void open() {
    iterator(getWars(territoryData), p -> territoryData.openMainMenu(player));
    gui.open(player);
  }

  public List<GuiItem> getWars(TerritoryData territoryData) {

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
              .setAction(event -> new WarMenu(player, territoryData, war))
              .asGuiItem(player, langType));
    }

    gui.setItem(4, 4, getAttackButton(territoryData));
    return guiItems;
  }

  private @NotNull GuiItem getAttackButton(TerritoryData territoryData) {
    return iconManager
        .get(new ItemStack(Material.BOW))
        .setName("Open Attacks")
        .setAction(p -> new AttackMenu(player, territoryData))
        .asGuiItem(player, langType);
  }
}
