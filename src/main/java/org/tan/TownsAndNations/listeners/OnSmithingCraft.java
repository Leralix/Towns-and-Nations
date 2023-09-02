package org.tan.TownsAndNations.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.inventory.ItemType;

public class OnSmithingCraft implements Listener {

    @EventHandler
    public void onSmithingCraft(PrepareSmithingEvent event){
        ItemStack[] contents = event.getInventory().getContents();

        // Le premier slot est pour l'ingrédient principal (outil/armure en diamant)
        // Le deuxième slot est pour l'ingrédient secondaire (lingot de netherite)
        ItemStack base = contents[0];
        ItemStack addition = contents[1];

        if (addition != null && addition.getType() == ItemType.NETHERITE_INGOT) {
            // Si l'ingrédient principal est un outil/armure en diamant, et le secondaire un lingot de netherite
            if (isDiamondGear(base)) {
                event.setResult(null);

            }
        }
    }

    private boolean isDiamondGear(ItemStack item) {
        if (item == null) return false;
        Material type = item.getData().getItemType();
        switch (type) {
            case DIAMOND_SWORD:
            case DIAMOND_PICKAXE:
            case DIAMOND_AXE:
            case DIAMOND_SHOVEL:
            case DIAMOND_HOE:
            case DIAMOND_HELMET:
            case DIAMOND_CHESTPLATE:
            case DIAMOND_LEGGINGS:
            case DIAMOND_BOOTS:
                return true;
            default:
                return false;
        }
    }

}
