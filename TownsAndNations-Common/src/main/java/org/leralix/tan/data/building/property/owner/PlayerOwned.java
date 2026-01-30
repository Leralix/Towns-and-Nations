package org.leralix.tan.data.building.property.owner;

import org.bukkit.Bukkit;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.economy.EconomyUtil;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.tan.api.interfaces.TanPlayer;

import java.util.UUID;

public class PlayerOwned extends AbstractOwner{

    private final String playerID;

    public PlayerOwned(ITanPlayer player) {
        this(player.getID().toString());
    }

    public PlayerOwned(String playerID) {
        super(OwnerType.PLAYER);
        this.playerID = playerID;
    }

    @Override
    public String getID() {
        return playerID;
    }

    @Override
    public String getName() {
        return Bukkit.getOfflinePlayer(UUID.fromString(playerID)).getName();
    }

    @Override
    public String getColoredName() {
        return getName();
    }

    @Override
    public boolean canAccess(TanPlayer tanPlayer) {
        return tanPlayer.getID().equals(playerID);
    }

    @Override
    public void addToBalance(double amount) {
        ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(playerID);
        EconomyUtil.addFromBalance(tanPlayer, amount);
    }

}
