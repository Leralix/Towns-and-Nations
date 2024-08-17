package org.tan.TownsAndNations.DataClass.wars.wargoals;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.tan.TownsAndNations.Lang.Lang;

public class SubjugateWarGoal extends WarGoal {

    @Override
    public ItemStack getIcon() {
        return buildIcon(Material.CHAIN, Lang.SUBJUGATE_WAR_GOAL_DESC.get());
    }

    @Override
    public String getDisplayName() {
        return Lang.SUBJUGATE_WAR_GOAL.get();
    }

    @Override
    public void applyWarGoal() {

    }
}
