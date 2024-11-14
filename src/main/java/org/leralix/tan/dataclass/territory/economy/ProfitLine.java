package org.leralix.tan.dataclass.territory.economy;

public abstract class ProfitLine {

    protected String getColoredMoney(double money){
        if(money > 0){
            return "§a+" + money + "§r";
        }else if(money < 0){
            return "§c-" + money + "§r";
        }
        return "§7" + money + "§r";
    }
    public abstract String getLine();
}
