package org.leralix.tan.listeners.interact.events.property;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.PropertyData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.gui.user.property.PlayerPropertyManager;

public class CreateTerritoryPropertyEvent extends CreatePropertyEvent {

  private final TownData teritoryData;

  public CreateTerritoryPropertyEvent(Player player, TownData townData) {
    super(player);
    teritoryData = townData;
  }

  @Override
  protected PropertyData createProperty() {
    PropertyData property = teritoryData.registerNewProperty(position1, position2, teritoryData);
    PlayerPropertyManager.open(player, property, HumanEntity::closeInventory);
    return property;
  }
}
