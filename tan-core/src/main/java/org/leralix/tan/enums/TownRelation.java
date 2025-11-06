package org.leralix.tan.enums;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.tan.api.enums.EDiplomacyState;

public enum TownRelation {
  SELF(8, Lang.RELATION_CITY_NAME, NamedTextColor.DARK_GREEN, false, false),
  OVERLORD(7, Lang.RELATION_OVERLORD_NAME, NamedTextColor.GREEN, false, false),
  VASSAL(6, Lang.RELATION_VASSAL_NAME, NamedTextColor.DARK_PURPLE, false, false),
  ALLIANCE(5, Lang.RELATION_ALLIANCE_NAME, NamedTextColor.BLUE, true, false),
  NON_AGGRESSION(4, Lang.RELATION_NON_AGGRESSION_NAME, NamedTextColor.DARK_AQUA, true, false),
  NEUTRAL(3, Lang.RELATION_NEUTRAL_NAME, NamedTextColor.GRAY, true, false),
  EMBARGO(2, Lang.RELATION_EMBARGO_NAME, NamedTextColor.GOLD, true, true),
  WAR(1, Lang.RELATION_HOSTILE_NAME, NamedTextColor.RED, true, true);

  private final int rank;
  private final Lang name;
  private final TextColor color;
  private final boolean canBeChanged;
  private final boolean negative;

  TownRelation(int rank, Lang name, TextColor color, boolean canBeChanged, boolean negative) {
    this.rank = rank;
    this.name = name;
    this.color = color;
    this.canBeChanged = canBeChanged;
    this.negative = negative;
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

  public boolean canBeChanged() {
    return canBeChanged;
  }

  public boolean isSuperiorTo(TownRelation oldRelation) {
    return rank > oldRelation.rank;
  }

  public EDiplomacyState toAPI() {
    return switch (this) {
      case ALLIANCE -> EDiplomacyState.ALLIANCE;
      case NON_AGGRESSION -> EDiplomacyState.NON_AGGRESSION;
      case NEUTRAL -> EDiplomacyState.NEUTRAL;
      case EMBARGO -> EDiplomacyState.EMBARGO;
      case WAR -> EDiplomacyState.WAR;
      default -> EDiplomacyState.NEUTRAL; // SELF, OVERLORD, VASSAL
    };
  }

  public static TownRelation fromAPI(EDiplomacyState state) {
    return switch (state) {
      case ALLIANCE -> ALLIANCE;
      case NON_AGGRESSION -> NON_AGGRESSION;
      case NEUTRAL -> NEUTRAL;
      case EMBARGO -> EMBARGO;
      case WAR -> WAR;
      default -> NEUTRAL; // SELF, OVERLORD, VASSAL
    };
  }

  public boolean isNegative() {
    return negative;
  }
}
