package org.leralix.tan.enums;

import org.bukkit.ChatColor;
import org.leralix.tan.dataclass.PlayerData;
import org.leralix.tan.dataclass.territory.ITerritoryData;

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
    public String getColoredName(){
        return this.color + this.name;
    }
    public TownChunkPermission getNext(){
        return this.next;
    }

    public boolean isAllowed(ITerritoryData ownerTown, PlayerData playerData) {
        switch (this) {
            case ALLIANCE -> {
                if(playerData.haveTown()){
                    if(ownerTown.getRelations().getRelationWith(playerData.getTown()) == TownRelation.ALLIANCE){
                        return true;
                    }
                }
                if(playerData.haveRegion()){
                    if(ownerTown.getRelations().getRelationWith(playerData.getRegion()) == TownRelation.ALLIANCE){
                        return true;
                    }
                }
            }
            case FOREIGN -> {
                return true;
            }
        }
        return false;
    }
}
