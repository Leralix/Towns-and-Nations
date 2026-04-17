package org.leralix.tan.listeners.interact.events.property;

import org.bukkit.entity.Player;
import org.leralix.tan.data.building.property.PropertyData;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.Town;

public class CreateTerritoryPropertyEvent extends CreatePropertyEvent {

    public CreateTerritoryPropertyEvent(Player player, ITanPlayer playerData, Town townData) {
        super(player, playerData, townData);
    }

    @Override
    protected PropertyData createProperty() {
        return townData.registerNewProperty(position1, position2);
    }

}
