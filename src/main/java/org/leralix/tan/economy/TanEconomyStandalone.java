package org.leralix.tan.economy;

import org.leralix.tan.dataclass.PlayerData;

public class TanEconomyStandalone extends AbstractTanEcon{
    @Override
    public int getBalance(PlayerData playerData) {
        return playerData.getBalance();
    }

    @Override
    public boolean has(PlayerData playerData, int v) {
        return playerData.getBalance() > v;
    }

    @Override
    public void withdrawPlayer(PlayerData playerData, int v) {
        playerData.removeFromBalance(v);
    }

    @Override
    public void depositPlayer(PlayerData playerData, int v) {
        playerData.addToBalance(v);
    }
}
