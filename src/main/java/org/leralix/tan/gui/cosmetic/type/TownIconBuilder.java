package org.leralix.tan.gui.cosmetic.type;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.tan.dataclass.PlayerData;
import org.leralix.tan.storage.stored.PlayerDataStorage;

public class TownIconBuilder extends IconBuilder {

    @Override
    protected ItemStack getItemStack(Player player) {
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        if(!playerData.hasTown()){
            return new ItemStack(Material.BARRIER);
        }
        return playerData.getTown().getIcon();
    }
}
