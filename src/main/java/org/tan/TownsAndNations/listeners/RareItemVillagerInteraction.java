package org.tan.TownsAndNations.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.enums.CustomVillagerProfession;
import org.tan.TownsAndNations.utils.ChatUtils;
import org.tan.TownsAndNations.utils.config.ConfigTag;
import org.tan.TownsAndNations.utils.config.ConfigUtil;
import org.tan.TownsAndNations.Economy.EconomyUtil;

import java.util.Set;

public class RareItemVillagerInteraction implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEntityEvent event){

        Player player = event.getPlayer();

        if (event.getRightClicked() instanceof Villager villager) {

            CustomVillagerProfession customProfession = null;

            Set<String> tags = villager.getScoreboardTags();
            if (!tags.isEmpty()) {
                String tag = tags.iterator().next();
                customProfession = CustomVillagerProfession.getVillager(tag);
            }

            if(customProfession == null){
                if(ConfigUtil.getCustomConfig(ConfigTag.MAIN).getBoolean("removeAllVillagerInteractions", false))
                    event.setCancelled(true);
                return;
            }
            event.setCancelled(true);


            int price = 0;
            if (customProfession == CustomVillagerProfession.GOLDSMITH ) {
                price = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("rareStoneValue");
            }
            if (customProfession == CustomVillagerProfession.BOTANIST ) {
                price = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("rareWoodValue");
            }
            if (customProfession == CustomVillagerProfession.COOK ) {
                price = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("rareCropsValue");
            }
            if(customProfession == CustomVillagerProfession.WIZARD){
                price = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("rareSoulValue");
            }
            if(customProfession == CustomVillagerProfession.FISHERMAN){
                price = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("rareFishValue");
            }

            ItemStack item = player.getInventory().getItemInMainHand();
            if (item.getItemMeta() == null){
                player.sendMessage(ChatUtils.getTANString() + Lang.RARE_ITEM_NO_ITEM_IN_HANDS.get(
                        customProfession.getBuyingItem().getItemMeta().getDisplayName(),
                        price)
                );
                return;
            }

            if(item.getType().equals(customProfession.getBuyingItem().getType())
                    && item.getItemMeta().getCustomModelData() == customProfession.getBuyingItem().getItemMeta().getCustomModelData()){

                int quantity = item.getAmount();
                String itemName = item.getItemMeta().getDisplayName();

                player.getInventory().setItemInMainHand(new ItemStack(Material.AIR, 1));

                EconomyUtil.addFromBalance(player, quantity * price);


                player.sendMessage(
                        ChatUtils.getTANString() +
                        Lang.RARE_ITEM_SELLING_SUCCESS.get(
                                quantity,
                                itemName,
                                quantity * price
                        )
                );
                return;
            }
            player.sendMessage(ChatUtils.getTANString() + Lang.RARE_ITEM_WRONG_ITEM.get(
                    customProfession.getBuyingItem().getItemMeta().getDisplayName()
            ));

        }



    }

}
