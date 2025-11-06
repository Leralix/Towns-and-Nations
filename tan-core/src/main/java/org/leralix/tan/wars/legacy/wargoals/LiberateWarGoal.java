package org.leralix.tan.wars.legacy.wargoals;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.gui.cosmetic.type.IconBuilder;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.utils.gameplay.TerritoryUtil;

public class LiberateWarGoal extends WarGoal {

  private final String territoryToLiberateID;

  public LiberateWarGoal(TerritoryData territoryToLiberate) {
    this.territoryToLiberateID = territoryToLiberate.getID();
  }

  @Override
  public IconBuilder getIcon(LangType langType) {

    List<FilledLang> description = new ArrayList<>();
    description.add(Lang.LIBERATE_SUBJECT_WAR_GOAL_DESC.get());
    description.add(Lang.LIBERATE_SUBJECT_WAR_GOAL_DESC1.get(getTerritoryToLiberate().getName()));

    return buildIcon(Material.LANTERN, description, langType);
  }

  @Override
  public String getDisplayName(LangType langType) {
    return Lang.LIBERATE_SUBJECT_WAR_GOAL.get(langType);
  }

  @Override
  public void applyWarGoal(TerritoryData winner, TerritoryData loser) {
    if (!getTerritoryToLiberate().haveOverlord()) {
      return;
    }
    getTerritoryToLiberate().removeOverlord();
  }

  @Override
  public boolean isCompleted() {
    return getTerritoryToLiberate() != null;
  }

  @Override
  public String getCurrentDesc(LangType langType) {
    if (getTerritoryToLiberate() == null) return null;
    return Lang.GUI_WARGOAL_LIBERATE_WAR_GOAL_RESULT.get(
        langType, getTerritoryToLiberate().getName());
  }

  public TerritoryData getTerritoryToLiberate() {
    return TerritoryUtil.getTerritory(territoryToLiberateID);
  }
}
