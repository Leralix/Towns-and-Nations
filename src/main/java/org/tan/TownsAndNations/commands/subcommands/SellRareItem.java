package org.tan.TownsAndNations.commands.subcommands;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.tan.TownsAndNations.DataClass.RareItem;
import org.tan.TownsAndNations.DataClass.newChunkData.ClaimedChunk2;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.commands.SubCommand;
import org.tan.TownsAndNations.listeners.RareItemDrops;
import org.tan.TownsAndNations.storage.DataStorage.NewClaimedChunkStorage;
import org.tan.TownsAndNations.utils.CustomNBT;
import org.tan.TownsAndNations.utils.DropChances;
import org.tan.TownsAndNations.utils.EconomyUtil;
import org.tan.TownsAndNations.utils.RareItemUtil;

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

        String rareItemTag = CustomNBT.getCustomStringTag(itemStack, "tanRareItem");

        if(rareItemTag == null){
            player.sendMessage(getTANString() + Lang.NOT_RARE_ITEM_IN_HAND.get());
            return;
        }

        int price = RareItemUtil.getPrice(rareItemTag);
        int quantity = itemStack.getAmount();

        player.sendMessage(Lang.RARE_ITEM_SELLING_SUCCESS.get(quantity,itemStack.getItemMeta().getDisplayName(), price * quantity));
        EconomyUtil.addFromBalance(player, price * itemStack.getAmount());
        player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
    }
}


