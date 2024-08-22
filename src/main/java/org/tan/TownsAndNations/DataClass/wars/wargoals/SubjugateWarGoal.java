package org.tan.TownsAndNations.DataClass.wars.wargoals;

import dev.triumphteam.gui.guis.Gui;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.tan.TownsAndNations.DataClass.wars.CreateAttackData;
import org.tan.TownsAndNations.Lang.Lang;

import java.util.function.Consumer;

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
    public void addExtraOptions(Gui gui, Player player, CreateAttackData createAttackData, Consumer<Player> exit) {

    }

    @Override
    public void applyWarGoal() {

    }

    @Override
    public boolean isCompleted() {
        return true;
    }

    @Override
    public String getCurrentDesc() {
        return Lang.GUI_WARGOAL_SUBJUGATE_WAR_GOAL_RESULT.get();
    }
}
