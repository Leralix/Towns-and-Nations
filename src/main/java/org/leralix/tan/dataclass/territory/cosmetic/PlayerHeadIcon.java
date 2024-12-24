package org.leralix.tan.dataclass.territory.cosmetic;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.UUID;

public class PlayerHeadIcon extends CustomIcon {
    String playerUUID;
    public PlayerHeadIcon(String playerID) {
        super(new ItemStack(Material.PLAYER_HEAD));
        this.playerUUID = playerID;
    }
    
    @Override
    public ItemStack getIcon() {
        ItemStack icon = new ItemStack(Material.PLAYER_HEAD);

        SkullMeta skullMeta = (SkullMeta) icon.getItemMeta();
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(playerUUID));

        if(skullMeta == null){
            return icon;
        }
        skullMeta.setOwningPlayer(offlinePlayer);
        icon.setItemMeta(skullMeta);
        return icon;
    }
    
    

}
