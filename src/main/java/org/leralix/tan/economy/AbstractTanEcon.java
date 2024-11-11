package org.leralix.tan.economy;

import org.leralix.tan.dataclass.PlayerData;

public abstract class AbstractTanEcon {

    public abstract double getBalance(PlayerData playerData);

    public abstract boolean has(PlayerData playerData, double amount);

    public abstract void withdrawPlayer(PlayerData playerData, double amount);

    public abstract void depositPlayer(PlayerData s, double amount);


}
