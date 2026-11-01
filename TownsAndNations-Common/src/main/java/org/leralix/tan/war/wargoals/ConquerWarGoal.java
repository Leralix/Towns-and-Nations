package org.leralix.tan.war.wargoals;


import org.bukkit.Material;
import org.leralix.tan.data.territory.Territory;
import org.leralix.tan.gui.cosmetic.type.IconBuilder;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.lang.Placeholder;
import org.tan.api.interfaces.war.wargoals.TanCaptureChunkWargoal;

import java.util.ArrayList;
import java.util.List;

public class ConquerWarGoal extends WarGoal implements TanCaptureChunkWargoal {

    private final int numberOfChunks;

    public ConquerWarGoal(int nbChunks) {
        numberOfChunks = nbChunks;
    }


    @Override
    public IconBuilder getIcon(LangType langType) {

        List<FilledLang> description = new ArrayList<>();
        description.add(Lang.CONQUER_WAR_GOAL_DESC.get());
        description.add(Lang.CONQUER_WAR_GOAL_DESC1.get(Integer.toString(numberOfChunks)));

        return buildIcon(Material.IRON_SWORD, description, langType);
    }

    @Override
    public String getDisplayName(LangType langType) {
        return Lang.CONQUER_WAR_GOAL.translate(langType);
    }

    @Override
    public void applyWarGoal(Territory winner, Territory loser) {
        if (winner == null || loser == null)
            return;
        winner.addAvailableClaims(loser.getID(), numberOfChunks);
    }

    @Override
    public Placeholder getCurrentDesc() {
        return Lang.GUI_CONQUER_CHUNK_CURRENT_DESC.get(Integer.toString(numberOfChunks));
    }

    @Override
    public int getAmount() {
        return numberOfChunks;
    }
}
