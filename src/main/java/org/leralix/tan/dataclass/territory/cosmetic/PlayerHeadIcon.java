package org.leralix.tan.dataclass.territory.cosmetic;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.leralix.tan.dataclass.ITanPlayer;

import java.util.UUID;

public class PlayerHeadIcon implements ICustomIcon {
    private final String playerUUID;

    public PlayerHeadIcon(ITanPlayer player) {
        if(player == null) {
           this.playerUUID = null;
        } else {
            this.playerUUID = player.getID();
        }
    }

    public PlayerHeadIcon(String playerID) {
        this.playerUUID = playerID;
    }
    
    public ItemStack getIcon() {
        if(playerUUID == null) return new ItemStack(Material.SKELETON_SKULL);

        return new ItemStack(Material.PLAYER_HEAD);
    }
    
    

}
