package org.leralix.tan.dataclass.territory.cosmetic;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CustomIcon {

    String materialTypeName;
    Integer customModelData;

    public CustomIcon(ItemStack icon) {
        this.materialTypeName = icon.getType().name();
        ItemMeta meta = icon.getItemMeta();
        if(meta != null && meta.hasCustomModelData())
            this.customModelData = meta.getCustomModelData();
    }

    public ItemStack getIcon() {
        ItemStack icon = new ItemStack(Material.getMaterial(materialTypeName));
        if(icon.getType() == Material.AIR){
            materialTypeName = Material.COBBLESTONE.name();
            icon = new ItemStack(Material.getMaterial(materialTypeName));
        }


        if(customModelData != null){
            ItemMeta meta = icon.getItemMeta();
            meta.setCustomModelData(customModelData);
            icon.setItemMeta(meta);
        }
        return icon;
    }
}
