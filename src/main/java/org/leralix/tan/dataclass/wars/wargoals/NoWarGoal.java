package org.leralix.tan.dataclass.wars.wargoals;

import dev.triumphteam.gui
.guis.Gui;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.tan.dataclass.wars.CreateAttackData;
import org.leralix.tan.lang.Lang;

import java.util.function.Consumer;

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
    public void addExtraOptions(Gui gui, Player player, CreateAttackData createAttackData) {

    }

    @Override
    public void applyWarGoal() {
        return;
    }

    @Override
    public boolean isCompleted() {
        return false;
    }

    @Override
    public String getCurrentDesc() {
        return Lang.NO_WAR_GOAL_SELECTED_DESC.get();
    }
}
