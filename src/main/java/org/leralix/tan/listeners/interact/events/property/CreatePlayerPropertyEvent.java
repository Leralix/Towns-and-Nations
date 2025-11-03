package org.leralix.tan.listeners.interact.events.property;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.PropertyData;
import org.leralix.tan.gui.user.property.PlayerPropertyManager;

public class CreatePlayerPropertyEvent extends CreatePropertyEvent {

    public CreatePlayerPropertyEvent(Player player) {
        super(player);
    }

    @Override
    protected PropertyData createProperty() {
        tanPlayer.removeFromBalance(cost);
        townData.addToBalance(cost);

        PropertyData property = townData.registerNewProperty(position1, position2, tanPlayer);
        new PlayerPropertyManager(player, property, HumanEntity::closeInventory);
        return property;
    }
}
