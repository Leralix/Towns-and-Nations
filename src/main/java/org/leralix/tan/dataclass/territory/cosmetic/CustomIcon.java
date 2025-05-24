package org.leralix.tan.dataclass.territory.cosmetic;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.swing.*;

public class CustomIcon implements ICustomIcon {

    String materialTypeName;
    Integer customModelData;

    public CustomIcon(ItemStack icon) {
        this.materialTypeName = icon.getType().name();
        ItemMeta meta = icon.getItemMeta();
        if(meta != null && meta.hasCustomModelData())
            this.customModelData = meta.getCustomModelData();
    }

    public ItemStack getIcon() {

        Material material = Material.getMaterial(materialTypeName);
        if(material == null){
            material = Material.BARRIER;
        }

        ItemStack icon = new ItemStack(material);
        if(icon.getType() == Material.AIR){
            materialTypeName = Material.COBBLESTONE.name();
            icon = new ItemStack(material);
        }


        if(customModelData != null){
            ItemMeta meta = icon.getItemMeta();
            meta.setCustomModelData(customModelData);
            icon.setItemMeta(meta);
        }
        return icon;
    }
}
