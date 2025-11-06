package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.guis.GuiItem;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.FortStorage;
import org.leralix.tan.wars.War;
import org.leralix.tan.wars.fort.Fort;
import org.leralix.tan.wars.legacy.WarRole;
import org.leralix.tan.wars.legacy.wargoals.CaptureFortWarGoal;

public class SelectFortForCapture extends IteratorGUI {

  private final WarRole warRole;
  private final TerritoryData territoryData;
  private final War war;

  private final TerritoryData enemyTerritoryData;

  public SelectFortForCapture(
      Player player, TerritoryData territoryData, War war, WarRole warRole) {
    super(player, Lang.HEADER_SELECT_WARGOAL.get(player), 3);
    this.warRole = warRole;
    this.territoryData = territoryData;
    this.war = war;
    this.enemyTerritoryData =
        war.isMainAttacker(territoryData) ? war.getMainDefender() : war.getMainAttacker();
    open();
  }

  @Override
  public void open() {
    iterator(getForts(), p -> new ChooseWarGoal(player, territoryData, war, warRole));
    gui.open(player);
  }

  private List<GuiItem> getForts() {

    List<GuiItem> items = new ArrayList<>();

    for (Fort fort : FortStorage.getInstance().getOwnedFort(enemyTerritoryData)) {
      items.add(
          iconManager
              .get(new ItemStack(Material.IRON_BLOCK))
              .setName(fort.getName())
              .setClickToAcceptMessage(Lang.GUI_GENERIC_CLICK_TO_SELECT)
              .setAction(
                  event -> {
                    war.addGoal(warRole, new CaptureFortWarGoal(fort));
                    new SelectWarGoals(player, territoryData, war, warRole);
                  })
              .asGuiItem(player, langType));
    }
    return items;
  }
}
