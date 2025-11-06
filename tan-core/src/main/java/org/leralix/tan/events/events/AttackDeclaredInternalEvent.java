package org.leralix.tan.events.events;

import org.leralix.tan.api.internal.wrappers.TerritoryDataWrapper;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.events.InternalEvent;
import org.tan.api.events.AttackDeclaredEvent;
import org.tan.api.interfaces.TanTerritory;

public class AttackDeclaredInternalEvent extends InternalEvent implements AttackDeclaredEvent {

  private final TerritoryData attackedTerritory;
  private final TerritoryData attackingTerritory;

  public AttackDeclaredInternalEvent(
      TerritoryData attackedTerritory, TerritoryData attackingTerritory) {
    super();
    this.attackedTerritory = attackedTerritory;
    this.attackingTerritory = attackingTerritory;
  }

  @Override
  public TanTerritory getDefenderTerritory() {
    return TerritoryDataWrapper.of(attackedTerritory);
  }

  @Override
  public TanTerritory getAttackerTerritory() {
    return TerritoryDataWrapper.of(attackingTerritory);
  }
}
