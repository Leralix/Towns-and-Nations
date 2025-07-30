package org.leralix.tan.war.legacy.wargoals;

import org.bukkit.Material;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.gui.cosmetic.type.IconBuilder;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.war.fort.Fort;

import java.util.ArrayList;
import java.util.List;

public class CaptureFortWarGoal extends WarGoal {

    private final Fort fortToCapture;

    public CaptureFortWarGoal(Fort fortToCapture){
        super();
        this.fortToCapture = fortToCapture;
    }

    @Override
    public IconBuilder getIcon(LangType langType) {

        List<String> description = new ArrayList<>();
        description.add(Lang.CAPTURE_FORT_WAR_GOAL_DESC.get(langType));
        description.add(Lang.CAPTURE_FORT_WAR_GOAL_DESC1.get(langType, fortToCapture.getName()));

        return buildIcon(Material.IRON_BLOCK, description);
    }

    @Override
    public String getDisplayName() {
        return Lang.CONQUER_WAR_GOAL.get();
    }

    @Override
    public void applyWarGoal(TerritoryData winner, TerritoryData looser) {

        fortToCapture.setOwner(winner);
        winner.addOwnedFort(fortToCapture);
        looser.removeOwnedFort(fortToCapture);
    }

    @Override
    public boolean isCompleted() {
        return true;
    }

    @Override
    public String getCurrentDesc() {
        return Lang.GUI_CAPTURE_LANDMARK_CURRENT_DESC.get(fortToCapture.getName());
    }

}
