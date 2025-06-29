package org.leralix.tan.enums;

import org.bukkit.ChatColor;
import org.leralix.tan.lang.Lang;
import org.tan.api.enums.EDiplomacyState;

public enum TownRelation {

    SELF(8, Lang.RELATION_CITY_NAME.get(), ChatColor.DARK_GREEN, false),
    OVERLORD(7,Lang.RELATION_OVERLORD_NAME.get(), ChatColor.GREEN, false),
    VASSAL(6,Lang.RELATION_VASSAL_NAME.get(), ChatColor.DARK_PURPLE, false),
    ALLIANCE(5,Lang.RELATION_ALLIANCE_NAME.get(), ChatColor.BLUE, true),
    NON_AGGRESSION(4,Lang.RELATION_NON_AGGRESSION_NAME.get(), ChatColor.DARK_AQUA, true),
    NEUTRAL(3,Lang.RELATION_NEUTRAL_NAME.get(), ChatColor.WHITE, true),
    EMBARGO(2,Lang.RELATION_EMBARGO_NAME.get(), ChatColor.GOLD, true),
    WAR(1,Lang.RELATION_HOSTILE_NAME.get(), ChatColor.RED, true);

    private final int rank;
    private final String name;
    private final ChatColor color;
    private final boolean canBeChanged;

    TownRelation(int rank, String name, ChatColor color, boolean canBeChanged) {
        this.rank = rank;
        this.name = name;
        this.color = color;
        this.canBeChanged = canBeChanged;
    }

    public String getName() {
        return name;
    }
    public ChatColor getColor() {
        return color;
    }
    public String getColoredName() {
        return color + name;
    }
    public boolean canBeChanged() {
        return canBeChanged;
    }

    public boolean isSuperiorTo(TownRelation oldRelation){
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
}