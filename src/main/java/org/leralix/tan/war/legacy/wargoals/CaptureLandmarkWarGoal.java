package org.leralix.tan.war.legacy.wargoals;

import org.bukkit.Material;
import org.leralix.tan.dataclass.Landmark;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.gui.cosmetic.type.IconBuilder;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;

import java.util.ArrayList;
import java.util.List;

public class CaptureLandmarkWarGoal extends WarGoal {

    private final Landmark landmarkToCapture;

    public CaptureLandmarkWarGoal(Landmark landmarkToCapture){
        super();
        this.landmarkToCapture = landmarkToCapture;
    }

    @Override
    public IconBuilder getIcon(LangType langType) {

        List<String> description = new ArrayList<>();
        description.add(Lang.CAPTURE_LANDMARK_WAR_GOAL_DESC.get(langType));
        description.add(Lang.CAPTURE_LANDMARK_WAR_GOAL_DESC1.get(langType, landmarkToCapture.getName()));


        return buildIcon(Material.DIAMOND, description);
    }

    @Override
    public String getDisplayName() {
        return Lang.CONQUER_WAR_GOAL.get();
    }

    @Override
    public void applyWarGoal(TerritoryData winner, TerritoryData territoryToSubjugate) {

        if(winner instanceof TownData winnerTown && territoryToSubjugate instanceof TownData townToSubjugate) {
            townToSubjugate.removeLandmark(landmarkToCapture);
            winnerTown.addLandmark(landmarkToCapture);
        }
    }

    @Override
    public boolean isCompleted() {
        return true;
    }

    @Override
    public String getCurrentDesc() {
        return Lang.GUI_CAPTURE_LANDMARK_CURRENT_DESC.get(landmarkToCapture.getName());
    }

}
