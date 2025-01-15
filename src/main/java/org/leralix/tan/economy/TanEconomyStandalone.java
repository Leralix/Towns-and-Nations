package org.leralix.tan.economy;

import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.dataclass.PlayerData;

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
        return ConfigUtil.getCustomConfig(ConfigTag.TAN).getString("moneyIcon");
    }
}
