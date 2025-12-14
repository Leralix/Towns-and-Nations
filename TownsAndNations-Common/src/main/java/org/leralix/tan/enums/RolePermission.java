package org.leralix.tan.enums;

import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;

public enum RolePermission {

    MANAGE_TAXES(false, IconKey.GUI_MANAGE_TAXES_ICON, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_MANAGE_TAXES),
    CLAIM_CHUNK(false, IconKey.GUI_CLAIM_CHUNK_ICON, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_CLAIM_CHUNK),
    UNCLAIM_CHUNK(false, IconKey.GUI_UNCLAIM_CHUNK_ICON, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_UNCLAIM_CHUNK),
    UPGRADE_TOWN(true, IconKey.GUI_UPGRADE_TOWN_ICON, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_UPGRADE_TOWN),
    INVITE_PLAYER(true, IconKey.GUI_INVITE_PLAYER_ICON, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_INVITE_PLAYER),
    KICK_PLAYER(true, IconKey.GUI_KICK_PLAYER_ICON, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_KICK_PLAYER),
    CREATE_RANK(false, IconKey.GUI_CREATE_RANK_ICON, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_CREATE_RANK),
    DELETE_RANK(false, IconKey.GUI_DELETE_RANK_ICON, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_DELETE_RANK),
    MANAGE_RANKS(false, IconKey.GUI_MANAGE_RANK_ICON, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_MODIFY_RANK),
    MANAGE_CLAIM_SETTINGS(true, IconKey.GUI_MANAGE_CLAIMS_ICON, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_MANAGE_CLAIM_SETTINGS),
    MANAGE_TOWN_RELATION(false, IconKey.GUI_MANAGE_DIPLOMACY_ICON, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_MANAGE_TOWN_RELATION), // Diplomacy
    MANAGE_WARS(false, IconKey.GUI_MANAGE_WAR_ICON, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_MANAGE_WAR),
    MANAGE_MOB_SPAWN(true, IconKey.GUI_MANAGE_MOB_SPAWN_ICON, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_MANAGE_MOB_SPAWN),
    CREATE_PROPERTY(false, IconKey.GUI_CREATE_PROPERTY_ICON, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_CREATE_PROPERTY),
    MANAGE_PROPERTY(false, IconKey.GUI_MANAGE_PROPERTY_ICON, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_MANAGE_PROPERTY), // Manage all buildings
    TOWN_ADMINISTRATOR(true, IconKey.GUI_ADMINISTRATOR_ICON, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_TOWN_ADMINISTRATOR),
    MANAGE_LANDMARK(false, IconKey.GUI_MANAGE_LANDMARK_ICON, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_MANAGE_LANDMARK),
    COLLECT_LANDMARK(false, IconKey.GUI_COLLECT_LANDMARK_ICON, Lang.GUI_TOWN_MEMBERS_ROLE_PRIORITY_COLLECT_LANDMARK);

    private final boolean onlyTown;
    private final IconKey iconKey;
    private final Lang name;


    RolePermission(boolean onlyTown, IconKey iconKey, Lang name) {
        this.onlyTown = onlyTown;
        this.iconKey = iconKey;
        this.name = name;
    }

    public boolean isForTerritory(TerritoryData territoryData) {
        if (territoryData instanceof TownData) {
            return true;
        }
        return !onlyTown;
    }

    public IconKey getIconKey() {
        return iconKey;
    }

    public Lang getName() {
        return name;
    }

    public String getName(LangType langType) {
        return name.get(langType);
    }
}