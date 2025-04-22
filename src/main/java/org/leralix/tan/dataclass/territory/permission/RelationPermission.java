package org.leralix.tan.dataclass.territory.permission;

import org.bukkit.ChatColor;
import org.leralix.tan.dataclass.PlayerData;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.enums.TownRelation;
import org.leralix.tan.lang.Lang;

public enum RelationPermission {

    TOWN(Lang.TOWN_PERMISSION.get(), ChatColor.GREEN),
    ALLIANCE(Lang.ALLIANCE_PERMISSION.get(), ChatColor.BLUE),
    FOREIGN(Lang.EVERYONE_PERMISSION.get(), ChatColor.WHITE),
    SELECTED_ONLY(Lang.EVERYONE_PERMISSION.get(), ChatColor.GRAY);

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
        FOREIGN.next = SELECTED_ONLY;
        SELECTED_ONLY.next = TOWN;
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
            case SELECTED_ONLY -> {
                return false;
            }
        }
        return false;
    }
}
