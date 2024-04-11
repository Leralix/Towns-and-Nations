package org.tan.TownsAndNations.DataClass;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.tan.TownsAndNations.storage.DataStorage.PlayerDataStorage;
import org.tan.TownsAndNations.utils.EconomyUtil;

import java.util.UUID;

public class Zone {
    private String ID;

    private String structureID;
    private String owningPlayerID;

    private final Vector3D p1;
    private final Vector3D p2;

    private String name;
    private String description;
    private boolean isForSale;
    private int salePrice;
    private boolean isForRent;
    private int rentPrice;



    public Zone(Vector3D p1, Vector3D p2, String structureID, Player player) {
        this.ID = getID();
        this.structureID = structureID;
        this.owningPlayerID = player.getUniqueId().toString();

        this.p1 = p1;
        this.p2 = p2;
    }

    private String getID() {
        return "0";
    }

    private String getOwnerID() {
        return owningPlayerID;
    }
    private PlayerData getOwner() {
        return PlayerDataStorage.get(getOwnerID());
    }

    private void sellZone(Player buyer){
        PlayerData buyerData = PlayerDataStorage.get(buyer.getUniqueId().toString());
        EconomyUtil.removeFromBalance(buyer, salePrice);

        OfflinePlayer seller = Bukkit.getOfflinePlayer(UUID.fromString(getOwnerID()));
        EconomyUtil.addFromBalance(seller, salePrice);

        owningPlayerID = buyer.getUniqueId().toString();
    }


}
