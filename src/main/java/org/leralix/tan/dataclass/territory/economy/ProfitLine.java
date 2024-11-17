package org.leralix.tan.dataclass.territory.economy;

import org.leralix.tan.economy.EconomyUtil;

public abstract class ProfitLine {

    protected String getColoredMoney(double money){
        String moneyChar = EconomyUtil.getMoneyIcon();
        if(money > 0){
            return "§a+" + money + moneyChar + "§r";
        }else if(money < 0){
            return "§c" + money + moneyChar + "§r";
        }
        return "§7" + money + moneyChar + "§r";
    }
    public abstract String getLine();
}
