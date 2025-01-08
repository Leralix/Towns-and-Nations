package org.leralix.tan.dataclass.territory.permission;

import org.bukkit.ChatColor;
import org.leralix.tan.dataclass.PlayerData;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.enums.TownRelation;

public enum RelationPermission {

    TOWN("Town", ChatColor.GREEN),
    ALLIANCE("Alliance", ChatColor.BLUE),
    FOREIGN("Foreign", ChatColor.GRAY);

    private final String name;
    private final ChatColor color;
    private RelationPermission next;

    RelationPermission(String name, ChatColor color) {
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
    public RelationPermission getNext(){
        return this.next;
    }


    public boolean isAllowed(TerritoryData territory, PlayerData playerData) {
        switch (this) {
            case TOWN -> {
                if(territory.isPlayerIn(playerData)){
                    return true;
                }
            }
            case ALLIANCE -> {
                if(territory.isPlayerIn(playerData)){
                    return true;
                }
                for(TerritoryData playerTerritory : playerData.getAllTerritoriesPlayerIsIn()){
                    if(territory.getRelations().getRelationWith(playerTerritory) == TownRelation.ALLIANCE){
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
