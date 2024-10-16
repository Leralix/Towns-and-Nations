package org.leralix.tan.economy;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.leralix.tan.dataclass.PlayerData;

import java.util.UUID;

public class TanEconomyExternal extends AbstractTanEcon {
    Economy externalEconomy;

    public TanEconomyExternal(Economy externalEconomy) {
        super();
        this.externalEconomy = externalEconomy;
    }
    @Override
    public int getBalance(PlayerData playerData) {
        UUID uuid = UUID.fromString(playerData.getID());
        return (int) externalEconomy.getBalance(Bukkit.getOfflinePlayer(uuid));
    }

    @Override
    public boolean has(PlayerData playerData, int v) {
        return getBalance(playerData) > v;
    }

    @Override
    public void withdrawPlayer(PlayerData playerData, int v) {
        UUID uuid = UUID.fromString(playerData.getID());
        externalEconomy.withdrawPlayer(Bukkit.getOfflinePlayer(uuid), v);
    }

    @Override
    public void depositPlayer(PlayerData s, int v) {
        UUID uuid = UUID.fromString(s.getID());
        externalEconomy.depositPlayer(Bukkit.getOfflinePlayer(uuid), v);
    }
}
