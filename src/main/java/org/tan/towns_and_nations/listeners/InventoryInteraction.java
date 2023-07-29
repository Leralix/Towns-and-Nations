package org.tan.towns_and_nations.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryInteraction implements Listener {

    @EventHandler
    public void onInventoryInteract(final InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) {
            return;
        }
        final Player p = (Player) e.getWhoClicked();
        final Inventory inv = e.getInventory();
        final ItemStack current = e.getCurrentItem();
        final ItemStack cursor = e.getCursor();

        if (e.getSlot() != 4)
            return;

        if(current == null || cursor == null)
            return;

        if(!current.getType().equals(Material.AIR) && !cursor.getType().equals(Material.AIR)) {
            p.sendMessage("current: " + current.getType());
            p.sendMessage("cursor: " + cursor.getType());
        }

    }
}
