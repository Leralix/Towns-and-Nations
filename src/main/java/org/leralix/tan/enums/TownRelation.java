package org.leralix.tan.enums;

import org.bukkit.ChatColor;
import org.leralix.tan.Lang.Lang;

public enum TownRelation {

    CITY(7,Lang.RELATION_CITY_NAME.get(), ChatColor.GREEN, Boolean.FALSE, Boolean.FALSE),
    REGION(6,Lang.RELATION_REGION_NAME.get(), ChatColor.AQUA, Boolean.FALSE , Boolean.FALSE),
    ALLIANCE(5,Lang.RELATION_ALLIANCE_NAME.get(), ChatColor.BLUE, Boolean.TRUE, Boolean.FALSE),
    NON_AGGRESSION(4,Lang.RELATION_NON_AGGRESSION_NAME.get(), ChatColor.DARK_AQUA, Boolean.TRUE, Boolean.FALSE),
    NEUTRAL(3,Lang.RELATION_NEUTRAL_NAME.get(), ChatColor.WHITE, Boolean.FALSE, Boolean.FALSE),
    EMBARGO(2,Lang.RELATION_EMBARGO_NAME.get(), ChatColor.GOLD, Boolean.FALSE, Boolean.TRUE),
    WAR(1,Lang.RELATION_HOSTILE_NAME.get(), ChatColor.RED, Boolean.FALSE, Boolean.TRUE);

    private final int rank;
    private final String name;
    private final ChatColor color;
    private final Boolean needsConfirmationToStart;
    private final Boolean needsConfirmationToEnd;

    TownRelation(int rank, String name, ChatColor color, Boolean needsConfirmationToStart, Boolean needsConfirmationToEnd){
        this.rank = rank;
        this.name = name;
        this.color = color;
        this.needsConfirmationToStart = needsConfirmationToStart;
        this.needsConfirmationToEnd = needsConfirmationToEnd;
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
    public Boolean getNeedsConfirmationToStart() {
        return needsConfirmationToStart;
    }
    public Boolean getNeedsConfirmationToEnd() {
        return needsConfirmationToEnd;
    }

    public boolean isRankSuperior(TownRelation oldRelation){
        return rank > oldRelation.rank;
    }
}