package org.leralix.tan.dataclass.territory.permission;

import org.bukkit.ChatColor;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.enums.TownRelation;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;

public enum RelationPermission {

    TOWN(Lang.TOWN_PERMISSION, ChatColor.GREEN),
    ALLIANCE(Lang.ALLIANCE_PERMISSION, ChatColor.BLUE),
    FOREIGN(Lang.EVERYONE_PERMISSION, ChatColor.DARK_GRAY),
    SELECTED_ONLY(Lang.SELECTED_ONLY_PERMISSION, ChatColor.GRAY);

    private final Lang name;
    private final ChatColor color;
    private RelationPermission next;

    RelationPermission(Lang name, ChatColor color) {
        this.name = name;
        this.color = color;
    }

    static {
        TOWN.next = ALLIANCE;
        ALLIANCE.next = FOREIGN;
        FOREIGN.next = SELECTED_ONLY;
        SELECTED_ONLY.next = TOWN;
    }

    public String getName(LangType langType) {
        return name.get(langType);
    }

    public ChatColor getColor() {
        return color;
    }

    public String getColoredName(LangType langType) {
        return this.color + getName(langType);
    }

    public RelationPermission getNext() {
        return this.next;
    }


    public boolean isAllowed(TerritoryData territoryToCheck, ITanPlayer tanPlayer) {
        switch (this) {
            case TOWN -> {
                return territoryToCheck.isPlayerIn(tanPlayer);
            }
            case ALLIANCE -> {
                if (territoryToCheck.isPlayerIn(tanPlayer)) {
                    return true;
                }
                for (TerritoryData playerTerritory : tanPlayer.getAllTerritoriesPlayerIsIn()) {
                    if (territoryToCheck.getRelations().getRelationWith(playerTerritory) == TownRelation.ALLIANCE) {
                        return true;
                    }
                }
                return false;
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
