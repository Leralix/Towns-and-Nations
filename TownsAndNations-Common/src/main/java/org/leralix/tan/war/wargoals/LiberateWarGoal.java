package org.leralix.tan.war.wargoals;

import org.bukkit.Material;
import org.leralix.tan.data.territory.TerritoryData;
import org.leralix.tan.gui.cosmetic.type.IconBuilder;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.utils.gameplay.TerritoryUtil;
import org.tan.api.interfaces.war.wargoals.TanLiberateWargoal;

import java.util.ArrayList;
import java.util.List;

public class LiberateWarGoal extends WarGoal implements TanLiberateWargoal {

    private final String territoryToLiberateID;

    public LiberateWarGoal(TerritoryData territoryToLiberate) {
        this.territoryToLiberateID = territoryToLiberate.getID();
    }

    @Override
    public IconBuilder getIcon(LangType langType) {

        List<FilledLang> description = new ArrayList<>();
        description.add(Lang.LIBERATE_SUBJECT_WAR_GOAL_DESC.get());
        description.add(Lang.LIBERATE_SUBJECT_WAR_GOAL_DESC1.get(getTerritoryToLiberate().getName()));

        return buildIcon(Material.LANTERN, description, langType);
    }

    @Override
    public String getDisplayName(LangType langType) {
        return Lang.LIBERATE_SUBJECT_WAR_GOAL.get(langType);
    }

    @Override
    public void applyWarGoal(TerritoryData winner, TerritoryData loser) {
        if(!getTerritoryToLiberate().haveOverlord()){
            return;
        }
        getTerritoryToLiberate().removeOverlord();
    }

    @Override
    public String getCurrentDesc(LangType langType) {
        if(getTerritoryToLiberate() == null)
            return null;
        return Lang.GUI_WARGOAL_LIBERATE_WAR_GOAL_RESULT.get(langType, getTerritoryToLiberate().getName());
    }

    @Override
    public TerritoryData getTerritoryToLiberate() {
        return TerritoryUtil.getTerritory(territoryToLiberateID);
    }
}
