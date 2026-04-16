package org.leralix.tan.war.wargoals;

import org.bukkit.Material;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.data.building.landmark.Landmark;
import org.leralix.tan.data.territory.Territory;
import org.leralix.tan.data.territory.Town;
import org.leralix.tan.gui.cosmetic.type.IconBuilder;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.tan.api.interfaces.buildings.TanLandmark;
import org.tan.api.interfaces.war.wargoals.TanCaptureLandmarkWargoal;

import java.util.ArrayList;
import java.util.List;

public class CaptureLandmarkWarGoal extends WarGoal implements TanCaptureLandmarkWargoal {

    private final String landmarkToCaptureID;

    public CaptureLandmarkWarGoal(Landmark landmarkToCapture){
        super();
        this.landmarkToCaptureID = landmarkToCapture.getID();
    }

    private Landmark getLandmark(){
        return TownsAndNations.getPlugin().getLandmarkStorage().get(landmarkToCaptureID);
    }

    @Override
    public TanLandmark getLandmarkToCapture() {
        return getLandmark();
    }

    @Override
    public IconBuilder getIcon(LangType langType) {

        List<FilledLang> description = new ArrayList<>();
        description.add(Lang.CAPTURE_LANDMARK_WAR_GOAL_DESC.get());
        description.add(Lang.GUI_CAPTURE_LANDMARK_CURRENT_DESC.get(getLandmarkToCapture().getName()));

        return buildIcon(Material.DIAMOND, description, langType);
    }

    @Override
    public String getDisplayName(LangType langType) {
        return Lang.CAPTURE_LANDMARK_WAR_GOAL.get(langType);
    }

    @Override
    public void applyWarGoal(Territory winner, Territory looser) {

        Landmark landmark = getLandmark();

        if(winner instanceof Town winnerTown &&
                landmark.isOwnedBy(looser))
        {
            landmark.removeOwnership();
            landmark.setOwner(winnerTown);
        }
    }

    @Override
    public String getCurrentDesc(LangType langType) {
        return Lang.GUI_CAPTURE_LANDMARK_CURRENT_DESC.get(langType, getLandmarkToCapture().getName());
    }

}
