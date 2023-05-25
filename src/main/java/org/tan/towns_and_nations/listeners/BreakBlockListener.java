package org.tan.towns_and_nations.listeners;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.tan.towns_and_nations.util.HeadUtils;

import java.util.Random;


public class BreakBlockListener implements Listener {




    @EventHandler
    public void onBreakBlock(BlockBreakEvent event){
        Player player = event.getPlayer();
        String blockName = event.getBlock().getType().name();



        ItemStack rareStoneItem = new ItemStack(Material.EMERALD);
        ItemMeta rareStoneItemMeta = rareStoneItem.getItemMeta();
        rareStoneItemMeta.setCustomModelData(101);
        rareStoneItemMeta.setDisplayName("Rare Stone");
        rareStoneItem.setItemMeta(rareStoneItemMeta);
        /*
        if(blockName.equals("GOLD_ORE")){
            Random rand = new Random();
            int int_random = rand.nextInt(100);

            if(int_random > 70){
                System.out.println("bingo");
                event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), rareStoneItem);

            }
        }

        if(blockName.equals("DEEPSLATE_GOLD_ORE")){
            Random rand = new Random();
            int int_random = rand.nextInt(100);

            if(int_random > 70){
                System.out.println("bingo");
                event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), rareStoneItem);

            }

        }

        */



    }



}
