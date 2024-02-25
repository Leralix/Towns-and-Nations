package org.tan.TownsAndNations.listeners;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.tan.TownsAndNations.DataClass.RareItem;
import org.tan.TownsAndNations.storage.NewClaimedChunkStorage;
import org.tan.TownsAndNations.storage.TownDataStorage;
import org.tan.TownsAndNations.utils.DropChances;

import java.util.Random;


public class RareItemDrops implements Listener {

    @EventHandler
    public void onBreakBlock(BlockBreakEvent event){
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();


        if(item.getEnchantments().containsKey(Enchantment.SILK_TOUCH))
            return;
        if(player.getGameMode() != GameMode.SURVIVAL)
            return;




        Block block = event.getBlock();
        Material type = block.getType();

        if(NewClaimedChunkStorage.isChunkClaimed(block.getChunk()) && !NewClaimedChunkStorage.isOwner(block.getChunk(), TownDataStorage.get(player).getID())){
            return; //chunk is claimed
        }

        if(type == Material.WHEAT || type == Material.BEETROOTS || type == Material.POTATOES || type == Material.CARROTS) {
            BlockData data = block.getBlockData();
            if(data instanceof Ageable) {
                Ageable ageable = (Ageable) data;

                if(ageable.getAge() < ageable.getMaximumAge()) {
                    return;

                }

            }
        }

        RareItem rareItem = DropChances.getRareItem(event.getBlock());
        if(rareItem == null)
            return;

        Random rand = new Random();
        int int_random = rand.nextInt(1, 100);

        if(int_random <= rareItem.getDropChance()){
            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), rareItem.getRareItem());
        }
    }

    @EventHandler
    public void onKillingMobs(EntityDeathEvent event){

        LivingEntity killer = event.getEntity().getKiller();

        if(killer instanceof Player){
            Player player = (Player) killer;

            RareItem rareItem = DropChances.getRareItem(event.getEntity());
            if(rareItem == null)
                return;
            Random rand = new Random();
            int int_random = rand.nextInt(1, 100);
            if(int_random <= rareItem.getDropChance()){
                event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), rareItem.getRareItem());
            }
        }
    }

}
