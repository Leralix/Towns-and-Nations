package org.tan.towns_and_nations.listeners;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Random;


public class BreakBlockListener implements Listener {




    @EventHandler
    public void onBreakBlock(BlockBreakEvent event){
        Player player = event.getPlayer();
        String blockName = event.getBlock().getType().name();

        ItemStack item = player.getInventory().getItemInMainHand();



        if(item.getEnchantments().containsKey(Enchantment.SILK_TOUCH)){
            return;
        }

        if(blockName.equals("GOLD_ORE")){
            Random rand = new Random();
            int int_random = rand.nextInt(100);

            if(int_random > 65){
                event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), getRareStone());

            }
        }

        if(blockName.equals("DEEPSLATE_GOLD_ORE")){
            Random rand = new Random();
            int int_random = rand.nextInt(100);

            if(int_random > 65){
                event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), getRareStone());

            }
        }

        if(blockName.equals("BIRCH_LOG")){
            Random rand = new Random();
            int int_random = rand.nextInt(100);

            if(int_random > 98  ){
                event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), getRareStone());

            }
        }

        if(blockName.equals("OAK_LOG")){
            Random rand = new Random();
            int int_random = rand.nextInt(100);

            if(int_random > 98  ){
                event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), getRareStone());

            }
        }



    }

    public ItemStack getRareStone(){
        ItemStack rareStoneItem = new ItemStack(Material.EMERALD);
        ItemMeta rareStoneItemMeta = rareStoneItem.getItemMeta();
        rareStoneItemMeta.setCustomModelData(101);
        rareStoneItemMeta.setDisplayName("Rare Stone");
        ArrayList<String> lore = new ArrayList<>();
        lore.add("Can be traded for money");
        rareStoneItemMeta.setLore(lore);
        rareStoneItem.setItemMeta(rareStoneItemMeta);
        return rareStoneItem;
    }

}
