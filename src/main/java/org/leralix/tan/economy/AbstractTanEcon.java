package org.leralix.tan.economy;

import org.leralix.tan.dataclass.ITanPlayer;

public abstract class AbstractTanEcon {

    public abstract double getBalance(ITanPlayer ITanPlayer);

    public abstract boolean has(ITanPlayer ITanPlayer, double amount);

    public abstract void withdrawPlayer(ITanPlayer ITanPlayer, double amount);

    public abstract void depositPlayer(ITanPlayer s, double amount);

    public abstract String getMoneyIcon();


}
