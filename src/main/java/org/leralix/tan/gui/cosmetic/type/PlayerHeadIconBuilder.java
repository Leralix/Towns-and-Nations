package org.leralix.tan.gui.cosmetic.type;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;

public class PlayerHeadIconBuilder extends IconBuilder{



    @Override
    protected ItemStack getItemStack(Player player) {
        PlayerProfile playerProfile = player.getPlayerProfile();
        return createSkull(playerProfile);
    }

    private ItemStack createSkull(PlayerProfile playerProfile) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        meta.setOwnerProfile(playerProfile);
        skull.setItemMeta(meta);
        return skull;
    }
}
