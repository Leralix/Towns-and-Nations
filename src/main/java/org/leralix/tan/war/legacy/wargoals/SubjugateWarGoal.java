package org.leralix.tan.war.legacy.wargoals;

import org.bukkit.Material;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.events.EventManager;
import org.leralix.tan.events.events.TerritoryVassalForcedInternalEvent;
import org.leralix.tan.gui.cosmetic.type.IconBuilder;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;

import java.util.ArrayList;
import java.util.List;

public class SubjugateWarGoal extends WarGoal {

    public SubjugateWarGoal() {
        super();
    }

    @Override
    public IconBuilder getIcon(LangType langType) {

        List<String> description = new ArrayList<>();
        description.add(Lang.SUBJUGATE_WAR_GOAL_DESC.get(langType));
        description.add(Lang.SUBJUGATE_WAR_GOAL_DESC1.get(langType));

        return buildIcon(Material.CHAIN, description, langType);
    }

    @Override
    public String getDisplayName(LangType langType) {
        return Lang.SUBJUGATE_WAR_GOAL.get(langType);
    }

    @Override
    public void applyWarGoal(TerritoryData winner, TerritoryData territoryToSubjugate) {
        if (territoryToSubjugate == null || winner == null)
            return;

        if (territoryToSubjugate.haveOverlord()) {
            territoryToSubjugate.removeOverlord();
        }
        territoryToSubjugate.setOverlord(winner);

        EventManager.getInstance().callEvent(new TerritoryVassalForcedInternalEvent(
                territoryToSubjugate,
                winner
        ));
    }

    @Override
    public boolean isCompleted() {
        return true;
    }

    @Override
    public String getCurrentDesc(LangType langType) {
        return Lang.GUI_WARGOAL_SUBJUGATE_WAR_GOAL_RESULT.get(langType);
    }

}
