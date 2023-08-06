package org.tan.towns_and_nations.enums;

import org.bukkit.ChatColor;

public enum TownRelation {

    CITY("City", ChatColor.GREEN),
    ALLIANCE("Alliance", ChatColor.BLUE),
    NON_AGGRESSION("Non-Aggression", ChatColor.YELLOW),
    EMBARGO("Embargo", ChatColor.GOLD),
    WAR("War", ChatColor.RED);

    private final String name;
    private final ChatColor color;

    TownRelation(String name, ChatColor color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public ChatColor getColor() {
        return color;
    }
}