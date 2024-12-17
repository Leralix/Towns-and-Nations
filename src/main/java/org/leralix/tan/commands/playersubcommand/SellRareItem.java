package org.leralix.tan.commands.playersubcommand;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.leralix.tan.economy.EconomyUtil;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.commands.SubCommand;
import org.leralix.tan.storage.CustomItemManager;
import org.leralix.tan.utils.*;

import java.util.Collections;
import java.util.List;

import static org.leralix.tan.utils.ChatUtils.getTANString;

public class SellRareItem extends SubCommand {
    @Override
    public String getName() {
        return "sell";
    }
    @Override
    public String getDescription() {
        return Lang.SELL_RARE_ITEM_COMMAND_DESC.get();
    }
    public int getArguments(){ return 1;}
    @Override
    public String getSyntax() {
        return "/tan sell";
    }
    public List<String> getTabCompleteSuggestions(Player player, String lowerCase, String[] args){
        return Collections.emptyList();
    }
    @Override
    public void perform(Player player, String[] args){
        if (args.length != 1){
            player.sendMessage(getTANString() +  Lang.CORRECT_SYNTAX_INFO.get(getSyntax()));
            return;
        }

        ItemStack itemStack = player.getInventory().getItemInMainHand();

        if (itemStack.getType() == Material.AIR){
            player.sendMessage(getTANString() + Lang.NO_ITEM_IN_HAND.get());
            return;
        }



        //Custom rare item check
        Integer value = CustomItemManager.getItemValue(itemStack);
        if(value == null){
            //Basic rare item check
            String rareItemTag = CustomNBT.getCustomStringTag(itemStack, "tanRareItem");
            if(rareItemTag == null){
                player.sendMessage(getTANString() + Lang.NOT_RARE_ITEM_IN_HAND.get());
                return;
            }
            value = RareItemUtil.getPrice(rareItemTag);
        }



        int quantity = itemStack.getAmount();
        ItemMeta itemMeta = itemStack.getItemMeta();
        player.sendMessage(Lang.RARE_ITEM_SELLING_SUCCESS.get(quantity,
                itemMeta.hasDisplayName() ? itemMeta.getDisplayName() : itemStack.getType().name(),
                value * quantity));
        EconomyUtil.addFromBalance(player, (double) value * itemStack.getAmount());
        player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
    }
}


