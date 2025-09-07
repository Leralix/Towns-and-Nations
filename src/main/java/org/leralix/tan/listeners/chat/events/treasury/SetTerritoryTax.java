package org.leralix.tan.listeners.chat.events.treasury;

import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.territory.TerritoryData;

import java.util.function.Consumer;

public class SetTerritoryTax extends SetSpecificTax {

    private final TerritoryData territoryData;

    public SetTerritoryTax(TerritoryData territoryData, Consumer<Player> guiCallback){
        super(guiCallback);
        this.territoryData = territoryData;
    }


    @Override
    protected void setTax(double amount) {
        territoryData.setTax(amount);
    }
}
