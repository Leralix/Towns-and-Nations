package org.leralix.tan.economy;

import org.leralix.tan.dataclass.PlayerData;
import org.leralix.tan.utils.config.ConfigTag;
import org.leralix.tan.utils.config.ConfigUtil;

public class TanEconomyStandalone extends AbstractTanEcon{
    @Override
    public double getBalance(PlayerData playerData) {
        return playerData.getBalance();
    }

    @Override
    public boolean has(PlayerData playerData, double amount) {
        return getBalance(playerData) > amount;
    }

    @Override
    public void withdrawPlayer(PlayerData playerData, double amount) {
        playerData.removeFromBalance(amount);
    }

    @Override
    public void depositPlayer(PlayerData playerData, double amount) {
        playerData.addToBalance(amount);
    }

    @Override
    public String getMoneyIcon() {
        return ConfigUtil.getCustomConfig(ConfigTag.MAIN).getString("moneyIcon");
    }
}
