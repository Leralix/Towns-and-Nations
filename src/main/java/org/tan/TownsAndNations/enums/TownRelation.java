package org.tan.TownsAndNations.enums;

import org.bukkit.ChatColor;
import org.tan.TownsAndNations.Lang.Lang;

public enum TownRelation {

    NEUTRAL(Lang.RELATION_NEUTRAL_NAME.get(), ChatColor.WHITE, Boolean.FALSE, Boolean.FALSE),
    CITY(Lang.RELATION_CITY_NAME.get(), ChatColor.GREEN, Boolean.FALSE, Boolean.FALSE),
    REGION(Lang.RELATION_REGION_NAME.get(), ChatColor.AQUA, Boolean.FALSE , Boolean.FALSE),
    ALLIANCE(Lang.RELATION_ALLIANCE_NAME.get(), ChatColor.BLUE, Boolean.TRUE, Boolean.FALSE),
    NON_AGGRESSION(Lang.RELATION_NON_AGGRESSION_NAME.get(), ChatColor.DARK_AQUA, Boolean.TRUE, Boolean.FALSE),
    EMBARGO(Lang.RELATION_EMBARGO_NAME.get(), ChatColor.GOLD, Boolean.FALSE, Boolean.TRUE),
    WAR(Lang.RELATION_HOSTILE_NAME.get(), ChatColor.RED, Boolean.FALSE, Boolean.TRUE);

    private final String name;
    private final ChatColor color;
    private final Boolean needsConfirmationToStart;
    private final Boolean needsConfirmationToEnd;

    TownRelation(String name, ChatColor color, Boolean needsConfirmationToStart, Boolean needsConfirmationToEnd){
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
}