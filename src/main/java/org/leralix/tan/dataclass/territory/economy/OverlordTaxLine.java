package org.leralix.tan.dataclass.territory.economy;

import dev.triumphteam.gui.guis.Gui;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.territory.ITerritoryData;
import org.leralix.tan.lang.Lang;

public class OverlordTaxLine extends ProfitLine {

    double tax;

    public OverlordTaxLine(ITerritoryData territoryData){
        super(territoryData);
        ITerritoryData overlordData = territoryData.getOverlord();
        if(overlordData == null)
            return;
        tax = -overlordData.getTax();
    }

    @Override
    public double getMoney() {
        return tax;
    }

    public String getLine() {
        return Lang.OVERLORD_TAX_LINE.get(getColoredMoney());
    }

    @Override
    public void addItems(Gui gui, Player player) {

    }


}
