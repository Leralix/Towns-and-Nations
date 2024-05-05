package org.tan.TownsAndNations.listeners;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.tan.TownsAndNations.DataClass.PlayerData;
import org.tan.TownsAndNations.DataClass.RareItem;
import org.tan.TownsAndNations.storage.DataStorage.NewClaimedChunkStorage;
import org.tan.TownsAndNations.storage.DataStorage.PlayerDataStorage;
import org.tan.TownsAndNations.utils.DropChances;


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

        //used to avoid spam breaking crops
        if(NewClaimedChunkStorage.isChunkClaimed(block.getChunk())){
            PlayerData playerData = PlayerDataStorage.get(player);
            if(!playerData.haveTown()){
                return; //player have no town
            }
            if(!NewClaimedChunkStorage.isOwner(block.getChunk(), playerData.getTownId()))
                return; //chunk is claimed by player's town
        }

        if(type == Material.WHEAT || type == Material.BEETROOTS || type == Material.POTATOES || type == Material.CARROTS) {
            BlockData data = block.getBlockData();
            if(data instanceof Ageable ageable) {
                if(ageable.getAge() < ageable.getMaximumAge()) {
                    return;
                }
            }
        }

        RareItem rareItem = DropChances.getRareItem(event.getBlock());

        if(rareItem != null)
            rareItem.spawn(event.getBlock().getWorld(), event.getBlock().getLocation());
    }
    @EventHandler
    public void onKillingMobs(EntityDeathEvent event){

        LivingEntity killer = event.getEntity().getKiller();

        if(killer != null){

            RareItem rareItem = DropChances.getRareItem(event.getEntity());
            if(rareItem != null)
                rareItem.spawn(event.getEntity().getWorld(), event.getEntity().getLocation());
        }
    }

    @EventHandler
    public void onFishing(PlayerFishEvent event){
        Player player = event.getPlayer();

        //Later add a check for fishing rod enchantment
        //ItemStack item = player.getInventory().getItemInMainHand();

        //if(player.getGameMode() != GameMode.SURVIVAL)
        //    return;

        if(!event.getHook().isInOpenWater())
            return;

        if(!event.getState().equals(PlayerFishEvent.State.CAUGHT_FISH))
            return;

        Entity entity = event.getCaught();
        if(entity == null)
            return;

        if(entity instanceof LivingEntity)
            return;

        Item fish = (Item)event.getCaught();
        ItemStack fishStack = fish.getItemStack();
        RareItem rareItem = DropChances.getRareItem(fishStack.getType().name());

        if(rareItem != null){
            Entity spawnedEntity = rareItem.spawn(event.getCaught().getWorld(), event.getCaught().getLocation());
            if (spawnedEntity != null) {
                Vector velocity = event.getHook().getVelocity();
                spawnedEntity.setVelocity(velocity);
            }
        }


    }
}
