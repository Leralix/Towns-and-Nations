package org.leralix.tan.listeners.chat.events.treasury;

import org.bukkit.entity.Player;
import org.leralix.tan.data.territory.Territory;

import java.util.function.Consumer;

public class SetTerritoryTax extends SetSpecificTax {

    private final Territory territoryData;

    public SetTerritoryTax(Territory territoryData, Consumer<Player> guiCallback){
        super(guiCallback);
        this.territoryData = territoryData;
    }


    @Override
    protected void setTax(double amount) {
        territoryData.setTax(amount);
    }
}
