package org.leralix.tan.listeners.chat.events.Treasury;

import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.territory.TerritoryData;

import java.util.function.Consumer;

public class SetCreatePropertyTax extends SetSpecificTax{

    private final TerritoryData territoryData;

    public SetCreatePropertyTax(TerritoryData territoryData, Consumer<Player> guiCallback){
        super(guiCallback);
        this.territoryData = territoryData;
    }

    @Override
    protected void setTax(double amount) {
        this.territoryData.setTaxOnCreatingProperty(amount);
    }
}
