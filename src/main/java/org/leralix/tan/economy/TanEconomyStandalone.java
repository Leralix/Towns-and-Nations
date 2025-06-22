package org.leralix.tan.economy;

import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.dataclass.ITanPlayer;

public class TanEconomyStandalone extends AbstractTanEcon{
    @Override
    public double getBalance(ITanPlayer tanPlayer) {
        return tanPlayer.getBalance();
    }

    @Override
    public boolean has(ITanPlayer tanPlayer, double amount) {
        return getBalance(tanPlayer) > amount;
    }

    @Override
    public void withdrawPlayer(ITanPlayer tanPlayer, double amount) {
        tanPlayer.removeFromBalance(amount);
    }

    @Override
    public void depositPlayer(ITanPlayer tanPlayer, double amount) {
        tanPlayer.addToBalance(amount);
    }

    @Override
    public String getMoneyIcon() {
        return ConfigUtil.getCustomConfig(ConfigTag.MAIN).getString("moneyIcon");
    }
}
