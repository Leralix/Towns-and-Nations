package org.leralix.tan.economy;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.leralix.tan.dataclass.ITanPlayer;

import java.util.UUID;

public class TanEconomyExternal extends AbstractTanEcon {
    Economy externalEconomy;

    public TanEconomyExternal(Economy externalEconomy) {
        super();
        this.externalEconomy = externalEconomy;
    }
    @Override
    public double getBalance(ITanPlayer ITanPlayer) {
        UUID uuid = UUID.fromString(ITanPlayer.getID());
        return externalEconomy.getBalance(Bukkit.getOfflinePlayer(uuid));
    }

    @Override
    public boolean has(ITanPlayer ITanPlayer, double v) {
        return getBalance(ITanPlayer) > v;
    }

    @Override
    public void withdrawPlayer(ITanPlayer ITanPlayer, double v) {
        UUID uuid = UUID.fromString(ITanPlayer.getID());
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
