package org.leralix.tan.economy;

import org.leralix.tan.dataclass.PlayerData;

public class TanEconomyStandalone extends AbstractTanEcon{
    @Override
    public double getBalance(PlayerData playerData) {
        return playerData.getBalance();
    }

    @Override
    public boolean has(PlayerData playerData, double amount) {
        return playerData.getBalance() > amount;
    }

    @Override
    public void withdrawPlayer(PlayerData playerData, double amount) {
        playerData.removeFromBalance(amount);
    }

    @Override
    public void depositPlayer(PlayerData playerData, double amount) {
        playerData.addToBalance(amount);
    }
}
