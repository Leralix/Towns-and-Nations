package org.tan.towns_and_nations.enums;

import org.bukkit.ChatColor;

public enum TownChunkPermission {

    TOWN("Town", ChatColor.GREEN),
    ALLIANCE("Alliance", ChatColor.BLUE),
    FOREIGN("Foreign", ChatColor.GRAY);
    private final String name;
    private final ChatColor color;
    TownChunkPermission(String name, ChatColor color){
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
