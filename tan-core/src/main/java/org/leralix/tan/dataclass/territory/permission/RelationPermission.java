package org.leralix.tan.dataclass.territory.permission;

import java.util.List;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.enums.TownRelation;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;

public enum RelationPermission {
  TOWN(Lang.TOWN_PERMISSION, NamedTextColor.GREEN),
  ALLIANCE(Lang.ALLIANCE_PERMISSION, NamedTextColor.BLUE),
  FOREIGN(Lang.EVERYONE_PERMISSION, NamedTextColor.WHITE),
  SELECTED_ONLY(Lang.SELECTED_ONLY_PERMISSION, NamedTextColor.GRAY);

  private final Lang name;
  private final TextColor color;
  private RelationPermission next;

  RelationPermission(Lang name, TextColor color) {
    this.name = name;
    this.color = color;
  }

  static {
    TOWN.next = ALLIANCE;
    ALLIANCE.next = FOREIGN;
    FOREIGN.next = SELECTED_ONLY;
    SELECTED_ONLY.next = TOWN;
  }

  public String getName(LangType langType) {
    return name.get(langType);
  }

  public TextColor getColor() {
    return color;
  }

  public String getColoredName(LangType langType) {
    return "<" + color.asHexString() + ">" + getName(langType);
  }

  public RelationPermission getNext() {
    return this.next;
  }

  public boolean isAllowed(TerritoryData territoryToCheck, ITanPlayer tanPlayer) {
    switch (this) {
      case TOWN -> {
        return territoryToCheck.isPlayerIn(tanPlayer);
      }
      case ALLIANCE -> {
        if (territoryToCheck.isPlayerIn(tanPlayer)) {
          return true;
        }
        List<TerritoryData> territories = tanPlayer.getAllTerritoriesPlayerIsInSync();
        if (territories != null) {
          for (TerritoryData playerTerritory : territories) {
            if (territoryToCheck.getRelations().getRelationWith(playerTerritory)
                == TownRelation.ALLIANCE) {
              return true;
            }
          }
        }
        return false;
      }
      case FOREIGN -> {
        return true;
      }
      case SELECTED_ONLY -> {
        return false;
      }
    }
    return false;
  }
}
