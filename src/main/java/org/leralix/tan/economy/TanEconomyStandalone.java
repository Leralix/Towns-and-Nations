package org.leralix.tan.economy;

import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.dataclass.ITanPlayer;

public class TanEconomyStandalone extends AbstractTanEcon{
    @Override
    public double getBalance(ITanPlayer ITanPlayer) {
        return ITanPlayer.getBalance();
    }

    @Override
    public boolean has(ITanPlayer ITanPlayer, double amount) {
        return getBalance(ITanPlayer) > amount;
    }

    @Override
    public void withdrawPlayer(ITanPlayer ITanPlayer, double amount) {
        ITanPlayer.removeFromBalance(amount);
    }

    @Override
    public void depositPlayer(ITanPlayer ITanPlayer, double amount) {
        ITanPlayer.addToBalance(amount);
    }

    @Override
    public String getMoneyIcon() {
        return ConfigUtil.getCustomConfig(ConfigTag.MAIN).getString("moneyIcon");
    }
}
