package org.leralix.tan.dataclass.territory.cosmetic;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.utils.ItemStackSerializer;

import javax.swing.*;

public class CustomIcon implements ICustomIcon {

    @Deprecated(since = "0.15.1", forRemoval = true)
    private String materialTypeName;
    @Deprecated(since = "0.15.1", forRemoval = true)
    private Integer customModelData;

    private String base64Item;

    public CustomIcon(ItemStack icon) {
        this.base64Item = ItemStackSerializer.serializeItemStack(icon);
    }

    public ItemStack getIcon() {

        if(base64Item == null){
            this.base64Item = ItemStackSerializer.serializeItemStack(getOldIcon());
        }
        return ItemStackSerializer.deserializeItemStack(base64Item);
    }

    private @NotNull ItemStack getOldIcon() {
        Material material = Material.getMaterial(materialTypeName);
        if(material == null){
            material = Material.BARRIER;
        }

        ItemStack icon = new ItemStack(material);
        if(icon.getType() == Material.AIR){
            materialTypeName = Material.COBBLESTONE.name();
            icon = new ItemStack(Material.COBBLESTONE);
        }


        if(customModelData != null){
            ItemMeta meta = icon.getItemMeta();
            meta.setCustomModelData(customModelData);
            icon.setItemMeta(meta);
        }
        return icon;
    }
}
