package org.leralix.tan.war.legacy.wargoals;


import org.bukkit.Material;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.gui.cosmetic.type.IconBuilder;
import org.leralix.tan.lang.Lang;

public class ConquerWarGoal extends WarGoal {

    int numberOfChunks;

    public ConquerWarGoal(){
        numberOfChunks = 1;
    }


    @Override
    public IconBuilder getIcon() {
        return buildIcon(Material.IRON_SWORD, Lang.CONQUER_WAR_GOAL_DESC.get());
    }

    @Override
    public String getDisplayName() {
        return Lang.CONQUER_WAR_GOAL.get();
    }

    @Override
    public void applyWarGoal(TerritoryData winner, TerritoryData loser) {
        if (winner == null)
            return;
        winner.addAvailableClaims(loser.getID(), numberOfChunks);
    }

    @Override
    public boolean isCompleted() {
        return true;
    }

    @Override
    public String getCurrentDesc() {
        return Lang.GUI_CONQUER_CHUNK_CURRENT_DESC.get(numberOfChunks);
    }


}
