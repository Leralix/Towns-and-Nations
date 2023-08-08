package org.tan.towns_and_nations.enums;

import org.bukkit.ChatColor;

public enum TownRelation {

    CITY("City", ChatColor.GREEN, Boolean.FALSE, Boolean.FALSE),
    ALLIANCE("Alliance", ChatColor.BLUE, Boolean.TRUE, Boolean.FALSE),
    NON_AGGRESSION("Non-Aggression", ChatColor.YELLOW, Boolean.TRUE, Boolean.FALSE),
    EMBARGO("Embargo", ChatColor.GOLD, Boolean.FALSE, Boolean.TRUE),
    WAR("War", ChatColor.RED, Boolean.FALSE, Boolean.TRUE);

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
    public Boolean getNeedsConfirmationToStart() {
        return needsConfirmationToStart;
    }
    public Boolean getNeedsConfirmationToEnd() {
        return needsConfirmationToEnd;
    }
}