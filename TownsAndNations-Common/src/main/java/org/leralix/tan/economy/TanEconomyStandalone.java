package org.leralix.tan.economy;

import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.utils.constants.Constants;

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
        return Constants.getBaseCurrencyChar();
    }
}
