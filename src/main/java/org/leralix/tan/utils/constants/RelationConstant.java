package org.leralix.tan.utils.constants;

import org.bukkit.configuration.ConfigurationSection;

import java.util.HashSet;
import java.util.Set;

public class RelationConstant {

    private final boolean canPvP;
    private final boolean haveTruce;
    private final boolean canInteractWithProperty;
    private final boolean canAccessTerritory;
    private final Set<String> blockedCommands;

    public RelationConstant(ConfigurationSection configurationSection){
        this.canPvP = configurationSection.getBoolean("CanPvP", true);
        this.haveTruce = configurationSection.getBoolean("haveTruce", true);
        this.canInteractWithProperty = configurationSection.getBoolean("canInteractWithProperty", true);
        this.canAccessTerritory = configurationSection.getBoolean("canAccessTerritory", true);
        this.blockedCommands = new HashSet<>(configurationSection.getStringList("blockedCommands"));
    }

    public boolean canPvP() {
        return canPvP;
    }

    public boolean haveTruce() {
        return haveTruce;
    }

    public boolean canInteractWithProperty() {
        return canInteractWithProperty;
    }

    public boolean canAccessTerritory() {
        return canAccessTerritory;
    }

    public Set<String> getBlockedCommands() {
        return blockedCommands;
    }
}
