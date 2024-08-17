package org.tan.TownsAndNations.DataClass.wars.wargoals;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.tan.TownsAndNations.Lang.Lang;

public class NoWarGoal extends WarGoal {

    @Override
    public ItemStack getIcon() {
        return buildIcon(Material.BARRIER, Lang.NO_WAR_GOAL_SELECTED_DESC.get());
    }

    @Override
    public String getDisplayName() {
        return Lang.NO_WAR_GOAL_SELECTED.get();
    }

    @Override
    public void applyWarGoal() {

    }
}
