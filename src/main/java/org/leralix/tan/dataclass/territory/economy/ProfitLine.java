package org.leralix.tan.dataclass.territory.economy;

import dev.triumphteam.gui.guis.Gui;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.territory.ITerritoryData;
import org.leralix.tan.economy.EconomyUtil;

public abstract class ProfitLine implements Comparable<ProfitLine> {

    protected final ITerritoryData territoryData;

    protected ProfitLine(ITerritoryData territoryData) {
        this.territoryData = territoryData;
    }


    public abstract double getMoney();
    protected String getColoredMoney(){
        String moneyChar = EconomyUtil.getMoneyIcon();
        double money = getMoney();
        if(money > 0){
            return "§a+" + money + moneyChar;
        }else if(money < 0){
            return "§c" + money + moneyChar;
        }
        return "§7" + money + moneyChar;
    }
    public abstract String getLine();

    public abstract void addItems(Gui gui, Player player);

    @Override
    public int compareTo(ProfitLine otherLine) {
        return Double.compare(otherLine.getMoney(), getMoney());
    }
}
