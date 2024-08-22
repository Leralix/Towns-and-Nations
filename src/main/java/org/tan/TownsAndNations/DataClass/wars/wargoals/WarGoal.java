package org.tan.TownsAndNations.DataClass.wars.wargoals;

import dev.triumphteam.gui.guis.Gui;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.tan.TownsAndNations.DataClass.wars.CreateAttackData;
import org.tan.TownsAndNations.Lang.Lang;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class WarGoal {

    String type;

    public WarGoal(){
        this.type = this.getClass().getSimpleName();
    }

    public abstract ItemStack getIcon();

    public abstract String getDisplayName();

    public abstract void addExtraOptions(Gui gui, Player player, CreateAttackData createAttackData, Consumer<Player> exit);

    public abstract void applyWarGoal();

    public abstract boolean isCompleted();

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

    public abstract String getCurrentDesc();
}
