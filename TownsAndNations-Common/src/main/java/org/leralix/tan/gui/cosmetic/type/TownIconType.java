package org.leralix.tan.gui.cosmetic.type;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.storage.stored.json.PlayerJsonStorage;

public class TownIconType extends IconType {

    @Override
    protected ItemStack getItemStack(Player player) {
        ITanPlayer tanPlayer = PlayerJsonStorage.getInstance().get(player);
        if(!tanPlayer.hasTown()){
            return new ItemStack(Material.BARRIER);
        }
        return tanPlayer.getTown().getIcon();
    }
}
