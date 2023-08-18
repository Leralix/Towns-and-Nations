package org.tan.towns_and_nations.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.tan.towns_and_nations.Lang.Lang;
import org.tan.towns_and_nations.enums.CustomVillagerProfession;
import org.tan.towns_and_nations.storage.PlayerStatStorage;
import org.tan.towns_and_nations.utils.ChatUtils;
import org.tan.towns_and_nations.utils.ConfigUtil;
import org.tan.towns_and_nations.utils.DropChances;
import org.tan.towns_and_nations.utils.MetaDataKeys;

public class RareItemVillagerInteraction implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEntityEvent event){

        Player player = event.getPlayer();

        if (event.getRightClicked() instanceof Villager villager) {

            String professionName = villager.getMetadata(MetaDataKeys.PROFESSION).get(0).asString();
            CustomVillagerProfession customProfession = CustomVillagerProfession.fromString(professionName);
            if(customProfession == null)
                return;

            int price = 0;
            if (customProfession == CustomVillagerProfession.GOLDSMITH ) {
                price = ConfigUtil.getCustomConfig("config.yml").getInt("rareStoneValue");
            }
            if (customProfession == CustomVillagerProfession.BOTANIST ) {
                price = ConfigUtil.getCustomConfig("config.yml").getInt("rareWoodValue");
            }
            if (customProfession == CustomVillagerProfession.COOK ) {
                price = ConfigUtil.getCustomConfig("config.yml").getInt("rareCropsValue");
            }

            ItemStack item = player.getInventory().getItemInMainHand();
            if (item.getItemMeta() == null){
                player.sendMessage(ChatUtils.getTANString() + Lang.RARE_ITEM_NO_ITEM_IN_HANDS.getTranslation(
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
                PlayerStatStorage.getStat(player).addToBalance(quantity * price);


                player.sendMessage(
                        ChatUtils.getTANString() +
                        Lang.RARE_ITEM_SELLING_SUCCESS.getTranslation(
                                quantity,
                                itemName,
                                quantity * price
                        )
                );
                return;
            }
            player.sendMessage(ChatUtils.getTANString() + Lang.RARE_ITEM_WRONG_ITEM.getTranslation(
                    customProfession.getBuyingItem().getItemMeta().getDisplayName()
            ));

        }



    }

}
