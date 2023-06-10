package org.tan.towns_and_nations.GUI;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public abstract class Inv implements InventoryHolder {
    protected Inventory inv;
    protected final Player viewer;

    public Inv(Player viewer, int size, String title) {
        this.inv = Bukkit.createInventory(this, size, title);
        this.viewer = viewer;
    }

    @Override
    public final Inventory getInventory() { return inv; }

    public abstract void build();
    public abstract void onClick(InventoryClickEvent event);
}

class InvListener implements Listener {

    @EventHandler
    public void onInvClick(InventoryClickEvent event) {
        var inv = event.getClickedInventory();
        if(inv == null) return;

        var holder = inv.getHolder();
        if(!(holder instanceof Inv menu)) return;

        event.setCancelled(true);
        menu.onClick(event);
    }
}