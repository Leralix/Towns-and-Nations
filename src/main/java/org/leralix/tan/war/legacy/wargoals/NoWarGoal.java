package org.leralix.tan.war.legacy.wargoals;

import org.bukkit.Material;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.gui.cosmetic.type.IconBuilder;
import org.leralix.tan.lang.Lang;

public class NoWarGoal extends WarGoal {

    @Override
    public IconBuilder getIcon() {
        return buildIcon(Material.BARRIER, Lang.NO_WAR_GOAL_SELECTED_DESC.get());
    }

    @Override
    public String getDisplayName() {
        return Lang.NO_WAR_GOAL_SELECTED.get();
    }

    @Override
    public void applyWarGoal(TerritoryData winner, TerritoryData loser) {
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
