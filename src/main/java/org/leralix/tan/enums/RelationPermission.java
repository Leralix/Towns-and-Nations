package org.leralix.tan.enums;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.leralix.tan.dataclass.PlayerData;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.utils.HeadUtils;

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


    public boolean isAllowed(TerritoryData ownerTown, PlayerData playerData) {
        switch (this) {

            case TOWN -> {
                if(ownerTown.isPlayerIn(playerData)){
                    return true;
                }
            }
            case ALLIANCE -> {
                if(playerData.haveTown() && ownerTown.getRelations().getRelationWith(playerData.getTown()) == TownRelation.ALLIANCE){
                        return true;
                    }
                if(playerData.haveRegion() && ownerTown.getRelations().getRelationWith(playerData.getRegion()) == TownRelation.ALLIANCE){
                        return true;
                    }
            }
            case FOREIGN -> {
                return true;
            }
            default -> {
                return false;
            }
        }
        return false;
    }
}
