package org.tan.TownsAndNations.DataClass.wars.wargoals;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.tan.TownsAndNations.Lang.Lang;

import java.util.ArrayList;
import java.util.List;

public abstract class WarGoal {


    public abstract ItemStack getIcon();

    public abstract String getDisplayName();

    public abstract void applyWarGoal();

    protected ItemStack buildIcon(Material material, String description){
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta != null){
            itemMeta.setDisplayName(getDisplayName());
            List<String> lore = new ArrayList<>();
            lore.add(description);
            lore.add(Lang.LEFT_CLICK_TO_SELECT.get());
            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);
        }

        return itemStack;
    }

}
