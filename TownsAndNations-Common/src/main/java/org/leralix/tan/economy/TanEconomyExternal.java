package org.leralix.tan.economy;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.leralix.tan.data.player.ITanPlayer;

import java.util.UUID;

public class TanEconomyExternal extends AbstractTanEcon {

    private final Economy externalEconomy;

    public TanEconomyExternal(Economy externalEconomy) {
        super();
        this.externalEconomy = externalEconomy;
    }

    @Override
    public double getBalance(ITanPlayer tanPlayer) {
        UUID uuid = tanPlayer.getID();
        return externalEconomy.getBalance(Bukkit.getOfflinePlayer(uuid));
    }

    @Override
    public boolean has(ITanPlayer tanPlayer, double v) {
        return getBalance(tanPlayer) > v;
    }

    @Override
    public void withdrawPlayer(ITanPlayer tanPlayer, double v) {
        externalEconomy.withdrawPlayer(Bukkit.getOfflinePlayer(tanPlayer.getID()), v);
    }

    @Override
    public void depositPlayer(ITanPlayer player, double v) {
        externalEconomy.depositPlayer(Bukkit.getOfflinePlayer(player.getID()), v);
    }

    @Override
    public String getMoneyIcon() {
        return externalEconomy.currencyNameSingular();
    }
}
