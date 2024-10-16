package org.leralix.tan.economy;

import org.leralix.tan.dataclass.PlayerData;

public abstract class AbstractTanEcon {

    public abstract int getBalance(PlayerData playerData);

    public abstract boolean has(PlayerData playerData, int v);

    public abstract void withdrawPlayer(PlayerData playerData, int v);

    public abstract void depositPlayer(PlayerData s, int v);


}
