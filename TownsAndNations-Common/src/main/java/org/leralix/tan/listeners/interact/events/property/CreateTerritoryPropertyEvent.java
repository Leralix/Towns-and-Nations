package org.leralix.tan.listeners.interact.events.property;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.PropertyData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.gui.user.property.PlayerPropertyManager;

public class CreateTerritoryPropertyEvent extends CreatePropertyEvent {

    private final TownData territoryData;

    public CreateTerritoryPropertyEvent(Player player, TownData townData) {
        super(player);
        territoryData = townData;
    }

    @Override
    protected PropertyData createProperty() {
        PropertyData property = territoryData.registerNewProperty(position1, position2, territoryData);
        new PlayerPropertyManager(player, property, HumanEntity::closeInventory);
        return property;
    }

}
