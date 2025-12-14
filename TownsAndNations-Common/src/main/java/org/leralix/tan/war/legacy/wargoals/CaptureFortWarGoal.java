package org.leralix.tan.war.legacy.wargoals;

import org.bukkit.Material;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.gui.cosmetic.type.IconBuilder;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.impl.FortDataStorage;
import org.leralix.tan.war.fort.Fort;
import org.tan.api.interfaces.war.wargoals.TanCaptureFortWargoal;

import java.util.ArrayList;
import java.util.List;

public class CaptureFortWarGoal extends WarGoal implements TanCaptureFortWargoal {

    private final String fortToCaptureID;

    public CaptureFortWarGoal(Fort fortToCapture){
        super();
        this.fortToCaptureID = fortToCapture.getID();
    }

    private Fort getFort(){
        return FortDataStorage.getInstance().getFort(fortToCaptureID);
    }

    @Override
    public IconBuilder getIcon(LangType langType) {

        List<FilledLang> description = new ArrayList<>();
        description.add(Lang.CAPTURE_FORT_WAR_GOAL_DESC.get());
        description.add(Lang.CAPTURE_FORT_WAR_GOAL_DESC1.get(getFort().getName()));

        return buildIcon(Material.IRON_BLOCK, description, langType);
    }

    @Override
    public String getDisplayName(LangType langType) {
        return Lang.CONQUER_WAR_GOAL.get(langType);
    }

    @Override
    public void applyWarGoal(TerritoryData winner, TerritoryData looser) {

        Fort fort = getFort();
        fort.setOwner(winner);
        winner.addOwnedFort(fort);
        looser.removeOwnedFort(fort);
    }

    @Override
    public String getCurrentDesc(LangType langType) {
        return Lang.GUI_CAPTURE_LANDMARK_CURRENT_DESC.get(langType, getFort().getName());
    }

}
