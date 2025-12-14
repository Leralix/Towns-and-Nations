package org.leralix.tan.war.legacy.wargoals;

import org.bukkit.Material;
import org.leralix.tan.dataclass.Landmark;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.gui.cosmetic.type.IconBuilder;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.stored.LandmarkStorage;

import java.util.ArrayList;
import java.util.List;

public class CaptureLandmarkWarGoal extends WarGoal {

    private final String landmarkToCaptureID;

    public CaptureLandmarkWarGoal(Landmark landmarkToCapture){
        super();
        this.landmarkToCaptureID = landmarkToCapture.getID();
    }

    private Landmark getLandmark(){
        return LandmarkStorage.getInstance().get(landmarkToCaptureID);
    }

    @Override
    public IconBuilder getIcon(LangType langType) {

        List<FilledLang> description = new ArrayList<>();
        description.add(Lang.CAPTURE_LANDMARK_WAR_GOAL_DESC.get());
        description.add(Lang.GUI_SELECTED_LANDMARK_TO_CAPTURE.get(getLandmark().getName()));

        return buildIcon(Material.DIAMOND, description, langType);
    }

    @Override
    public String getDisplayName(LangType langType) {
        return Lang.CONQUER_WAR_GOAL.get(langType);
    }

    @Override
    public void applyWarGoal(TerritoryData winner, TerritoryData looser) {

        Landmark landmark = getLandmark();

        if(winner instanceof TownData winnerTown && 
                landmark.isOwnedBy(looser))
        {
            landmark.removeOwnership();
            landmark.setOwner(winnerTown);
        }
    }

    @Override
    public boolean isCompleted() {
        return true;
    }

    @Override
    public String getCurrentDesc(LangType langType) {
        return Lang.GUI_CAPTURE_LANDMARK_CURRENT_DESC.get(Lang.getServerLang(), getLandmark().getName());
    }

}
