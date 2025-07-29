package org.leralix.tan.war.legacy.wargoals;

import org.bukkit.Material;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.events.EventManager;
import org.leralix.tan.events.events.TerritoryVassalForcedInternalEvent;
import org.leralix.tan.gui.cosmetic.type.IconBuilder;
import org.leralix.tan.lang.Lang;

public class SubjugateWarGoal extends WarGoal {

    public SubjugateWarGoal() {
        super();
    }

    @Override
    public IconBuilder getIcon() {
        return buildIcon(Material.CHAIN, Lang.SUBJUGATE_WAR_GOAL_DESC.get());
    }

    @Override
    public String getDisplayName() {
        return Lang.SUBJUGATE_WAR_GOAL.get();
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
    public String getCurrentDesc() {
        return Lang.GUI_WARGOAL_SUBJUGATE_WAR_GOAL_RESULT.get();
    }

}
