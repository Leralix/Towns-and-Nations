package org.leralix.tan.listeners.interact.events.property;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.leralix.tan.data.building.property.PropertyData;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.TownData;
import org.leralix.tan.gui.user.property.PlayerPropertyManager;

public class CreateTerritoryPropertyEvent extends CreatePropertyEvent {

    public CreateTerritoryPropertyEvent(Player player, ITanPlayer playerData, TownData townData) {
        super(player, playerData, townData);
    }

    @Override
    protected PropertyData createProperty() {
        PropertyData property = townData.registerNewProperty(position1, position2);
        new PlayerPropertyManager(player, property, HumanEntity::closeInventory);
        return property;
    }

}
