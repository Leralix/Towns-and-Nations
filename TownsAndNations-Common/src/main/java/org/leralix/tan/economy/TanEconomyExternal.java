package org.leralix.tan.economy;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.leralix.tan.data.player.ITanPlayer;

import java.util.UUID;

public class TanEconomyExternal extends AbstractTanEcon {
    Economy externalEconomy;

    public TanEconomyExternal(Economy externalEconomy) {
        super();
        this.externalEconomy = externalEconomy;
    }
    @Override
    public double getBalance(ITanPlayer tanPlayer) {
        UUID uuid = UUID.fromString(tanPlayer.getID());
        return externalEconomy.getBalance(Bukkit.getOfflinePlayer(uuid));
    }

    @Override
    public boolean has(ITanPlayer tanPlayer, double v) {
        return getBalance(tanPlayer) > v;
    }

    @Override
    public void withdrawPlayer(ITanPlayer tanPlayer, double v) {
        UUID uuid = UUID.fromString(tanPlayer.getID());
        externalEconomy.withdrawPlayer(Bukkit.getOfflinePlayer(uuid), v);
    }

    @Override
    public void depositPlayer(ITanPlayer s, double v) {
        UUID uuid = UUID.fromString(s.getID());
        externalEconomy.depositPlayer(Bukkit.getOfflinePlayer(uuid), v);
    }

    @Override
    public String getMoneyIcon() {
        return externalEconomy.currencyNameSingular();
    }
}
