package org.leralix.tan.listeners.interact;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.HashMap;

public class RightClickListener implements Listener {

    private static final HashMap<Player, RightClickListenerEvent> events = new HashMap<>();


    @EventHandler
    public void OnPlayerInteractEvent(PlayerInteractEvent event) {

        if(event.getHand() == EquipmentSlot.OFF_HAND)
            return;

        if(event.getItem() != null){
            return;
        }

        Player player = event.getPlayer();
        if(events.containsKey(player)){
            event.setCancelled(true);
            events.get(player).execute(event);
        }
    }

    public static void removePlayer(Player player){
        events.remove(player);
    }

    public static void register(Player player, RightClickListenerEvent rightClickListenerEvent){
        events.put(player, rightClickListenerEvent);
    }

}
