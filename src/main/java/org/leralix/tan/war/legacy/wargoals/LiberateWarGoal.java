package org.leralix.tan.war.legacy.wargoals;

import org.bukkit.Material;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.gui.cosmetic.type.IconBuilder;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;

import java.util.ArrayList;
import java.util.List;

public class LiberateWarGoal extends WarGoal {

    TerritoryData territoryToLiberate;

    @Override
    public IconBuilder getIcon(LangType langType) {

        List<String> description = new ArrayList<>();
        description.add(Lang.LIBERATE_SUBJECT_WAR_GOAL_DESC.get(langType));
        description.add(Lang.LIBERATE_SUBJECT_WAR_GOAL_DESC1.get(langType, territoryToLiberate.getName()));
        description.add(Lang.GUI_GENERIC_RIGHT_CLICK_TO_DELETE.get(langType));

        return buildIcon(Material.LANTERN, description);
    }

    @Override
    public String getDisplayName() {
        return Lang.LIBERATE_SUBJECT_WAR_GOAL.get();
    }

    @Override
    public void applyWarGoal(TerritoryData winner, TerritoryData loser) {
        if(!territoryToLiberate.haveOverlord())
            return;
        territoryToLiberate.removeOverlord();
    }

    @Override
    public boolean isCompleted() {
        return territoryToLiberate != null;
    }

    @Override
    public String getCurrentDesc() {
        if(territoryToLiberate == null)
            return null;
        return Lang.GUI_WARGOAL_LIBERATE_WAR_GOAL_RESULT.get();
    }

    public void setTerritoryToLiberate(TerritoryData territoryToLiberate) {
        this.territoryToLiberate = territoryToLiberate;
    }
}
