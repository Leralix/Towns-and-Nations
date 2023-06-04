package org.tan.towns_and_nations.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.tan.towns_and_nations.utils.PlayerStatStorage;
import org.tan.towns_and_nations.utils.TownDataStorage;

public class VillagerInteractionListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEntityEvent event){

        Player player = event.getPlayer();


        if (event.getRightClicked() instanceof Villager) {
            Villager villager = (Villager) event.getRightClicked();

            if (villager.getCustomName() != null && villager.getCustomName().equals("Goldsmith")) {

                ItemStack item = player.getInventory().getItemInMainHand();


                if(item.getItemMeta() == null){
                    return;
                }

                if(item.getType().toString().equals("EMERALD") && item.getItemMeta().getDisplayName().equals("Rare Stone") && item.getItemMeta().getCustomModelData() == 101){
                    player.sendMessage("You sold " + item.getAmount() + " emeralds for " + item.getAmount() + " $");
                    player.getInventory().setItemInMainHand(new ItemStack( Material.AIR,1));
                    PlayerStatStorage.findStatUUID(player.getUniqueId().toString()).addToBalance(item.getAmount());
                    return;
                }

                player.sendMessage("This villager is only buying Rare Stone");

            }
        }



    }

}
