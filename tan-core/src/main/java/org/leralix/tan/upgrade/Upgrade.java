package org.leralix.tan.upgrade;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.gui.service.requirements.IndividualRequirement;
import org.leralix.tan.gui.service.requirements.upgrade.UpgradeRequirement;
import org.leralix.tan.lang.DynamicLang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.upgrade.rewards.IndividualStat;

public class Upgrade {

  private final int row;
  private final int column;
  private final Material iconMaterial;
  private final String nameKey;
  private final int maxLevel;
  private final List<UpgradeRequirement> upgradeRequirements;
  private final List<IndividualStat> rewards;

  public Upgrade(
      int row,
      int column,
      String nameKey,
      Material iconMaterial,
      int maxLevel,
      List<UpgradeRequirement> upgradeRequirements,
      List<IndividualStat> rewards) {
    this.row = row;
    this.column = column;
    this.nameKey = nameKey;
    this.iconMaterial = iconMaterial;
    this.maxLevel = maxLevel;
    this.upgradeRequirements = upgradeRequirements;
    this.rewards = rewards;
  }

  public Material getIconMaterial() {
    return iconMaterial;
  }

  public String getID() {
    return nameKey;
  }

  public String getName(LangType langType) {
    return DynamicLang.get(langType, nameKey);
  }

  public Collection<IndividualRequirement> getRequirements(
      TerritoryData territoryData, Player player) {

    List<IndividualRequirement> res = new ArrayList<>();
    for (UpgradeRequirement upgradeRequirement : upgradeRequirements) {
      res.add(upgradeRequirement.toIndividualRequirement(this, territoryData, player));
    }
    return res;
  }

  public int getRow() {
    return row;
  }

  public int getColumn() {
    return column;
  }

  public int getMaxLevel() {
    return maxLevel;
  }

  public Collection<IndividualStat> getRewards() {
    return rewards;
  }
}
