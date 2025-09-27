package org.leralix.tan.war.legacy.wargoals;


import org.bukkit.Material;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.gui.cosmetic.type.IconBuilder;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;

import java.util.ArrayList;
import java.util.List;

public class ConquerWarGoal extends WarGoal {

    private final int numberOfChunks;

    public ConquerWarGoal(int nbChunks) {
        numberOfChunks = nbChunks;
    }


    @Override
    public IconBuilder getIcon(LangType langType) {

        List<String> description = new ArrayList<>();
        description.add(Lang.CONQUER_WAR_GOAL_DESC.get(langType));
        description.add(Lang.CONQUER_WAR_GOAL_DESC1.get(langType, Integer.toString(numberOfChunks)));
        description.add(Lang.GUI_GENERIC_RIGHT_CLICK_TO_DELETE.get(langType));

        return buildIcon(Material.IRON_SWORD, description, langType);
    }

    @Override
    public String getDisplayName(LangType langType) {
        return Lang.CONQUER_WAR_GOAL.get(langType);
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
    public String getCurrentDesc(LangType langType) {
        return Lang.GUI_CONQUER_CHUNK_CURRENT_DESC.get(langType, Integer.toString(numberOfChunks));
    }


}
