package org.leralix.tan.listeners.interact.events.property;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.leralix.tan.data.building.property.PropertyData;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.TownData;
import org.leralix.tan.gui.user.property.PlayerPropertyManager;
import org.leralix.tan.storage.database.transactions.TransactionManager;
import org.leralix.tan.storage.database.transactions.instance.CreatingPropertyTransaction;

public class CreatePlayerPropertyEvent extends CreatePropertyEvent {

    public CreatePlayerPropertyEvent(Player player, ITanPlayer playerData, TownData townData) {
        super(player, playerData, townData);
    }

    @Override
    protected PropertyData createProperty() {
        tanPlayer.removeFromBalance(cost);
        townData.addToBalance(cost);

        PropertyData property = townData.registerNewProperty(position1, position2, tanPlayer);

        TransactionManager.getInstance().register(
                new CreatingPropertyTransaction(
                        townData.getID(),
                        property.getPropertyID(),
                        player.getUniqueId().toString(),
                        cost,
                        townData.getTaxOnCreatingProperty()
                )
        );

        new PlayerPropertyManager(player, property, HumanEntity::closeInventory);
        return property;
    }
}
