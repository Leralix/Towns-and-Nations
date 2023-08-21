package org.tan.TownsAndNations.enums;

import org.bukkit.ChatColor;

public enum TownChunkPermission {

    TOWN("Town", ChatColor.GREEN),
    ALLIANCE("Alliance", ChatColor.BLUE),
    FOREIGN("Foreign", ChatColor.GRAY);

    private final String name;
    private final ChatColor color;
    private TownChunkPermission next;

    TownChunkPermission(String name, ChatColor color) {
        this.name = name;
        this.color = color;
    }

    static {
        TOWN.next = ALLIANCE;
        ALLIANCE.next = FOREIGN;
        FOREIGN.next = TOWN;
    }

    public String getName() {
        return name;
    }

    public ChatColor getColor() {
        return color;
    }
    public TownChunkPermission getNext(){
        return this.next;
    }

}
