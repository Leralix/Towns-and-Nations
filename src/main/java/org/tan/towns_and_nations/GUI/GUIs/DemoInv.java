package org.tan.towns_and_nations.GUI.GUIs;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.tan.towns_and_nations.GUI.Inv;

class DemoInv extends Inv {
    public DemoInv(Player viewer) {
        super(viewer, 9, "Demo Inventory");
        build();
    }

    public void build() {
        inv.setItem(0, new ItemStack(Material.DIAMOND));
        viewer.openInventory(inv);
    }

    public void onClick(InventoryClickEvent event) {
        var it = event.getCurrentItem();
        if(it != null && it.getType() == Material.DIAMOND) {
            viewer.sendMessage("Diamond clicked");
        }
    }
}