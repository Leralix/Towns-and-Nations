package org.tan.TownsAndNations.DataClass.wars.wargoals;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.tan.TownsAndNations.Lang.Lang;

public class ConquerWarGoal extends WarGoal {

    @Override
    public ItemStack getIcon() {
        return buildIcon(Material.IRON_SWORD, Lang.CONQUER_WAR_GOAL_DESC.get());
    }

    @Override
    public String getDisplayName() {
        return Lang.CONQUER_WAR_GOAL.get();
    }

    @Override
    public void applyWarGoal() {

    }
}
