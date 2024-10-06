package org.tan.TownsAndNations.commands.subcommands;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.tan.TownsAndNations.Economy.EconomyUtil;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.commands.SubCommand;
import org.tan.TownsAndNations.storage.CustomItemManager;
import org.tan.TownsAndNations.utils.*;

import java.util.List;

import static org.tan.TownsAndNations.utils.ChatUtils.getTANString;

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
    public List<String> getTabCompleteSuggestions(Player player, String[] args){
        return null;
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
        EconomyUtil.addFromBalance(player, value * itemStack.getAmount());
        player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
    }
}


