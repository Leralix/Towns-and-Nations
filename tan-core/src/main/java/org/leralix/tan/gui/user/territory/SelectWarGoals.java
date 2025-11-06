package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.guis.GuiItem;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.user.war.WarMenu;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.wars.War;
import org.leralix.tan.wars.legacy.WarRole;
import org.leralix.tan.wars.legacy.wargoals.WarGoal;

public class SelectWarGoals extends IteratorGUI {

  private final TerritoryData territoryData;
  private final War war;
  private final WarRole warRole;

  public SelectWarGoals(Player player, TerritoryData territoryData, War war, WarRole warRole) {
    super(player, Lang.HEADER_SELECT_WARGOAL.get(player), 3);
    this.territoryData = territoryData;
    this.war = war;
    this.warRole = warRole;
    open();
  }

  @Override
  public void open() {
    iterator(getWarGoals(), p -> new WarMenu(player, territoryData, war));

    gui.setItem(3, 5, getNewWarGoalButton());

    gui.open(player);
  }

  private @NotNull GuiItem getNewWarGoalButton() {
    return iconManager
        .get(IconKey.NEW_WAR_GOAL_ICON)
        .setName(Lang.GUI_ADD_WAR_GOAL.get(langType))
        .setClickToAcceptMessage(Lang.GUI_GENERIC_CLICK_TO_PROCEED)
        .setAction(action -> new ChooseWarGoal(player, territoryData, war, warRole))
        .asGuiItem(player, langType);
  }

  private List<GuiItem> getWarGoals() {

    List<GuiItem> items = new ArrayList<>();
    for (WarGoal goal : war.getGoals(warRole)) {
      items.add(
          goal.getIcon(langType)
              .setAction(
                  event -> {
                    if (event.isRightClick()) {
                      war.removeGoal(warRole, goal);
                      open();
                    }
                  })
              .asGuiItem(player, langType));
    }
    return items;
  }
}
