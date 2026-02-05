package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.leralix.tan.data.building.landmark.Landmark;
import org.leralix.tan.data.territory.TerritoryData;
import org.leralix.tan.data.territory.TownData;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.stored.LandmarkStorage;
import org.leralix.tan.war.War;
import org.leralix.tan.war.info.WarRole;
import org.leralix.tan.war.wargoals.CaptureLandmarkWarGoal;

import java.util.ArrayList;
import java.util.List;

public class SelectLandmarkForCapture extends IteratorGUI {

    private final WarRole warRole;
    private final War war;
    private final BasicGui returnGui;

    private final TownData enemyTownData;

    public SelectLandmarkForCapture(Player player, TerritoryData territoryData, War war, WarRole warRole, BasicGui returnGui) {
        super(player, Lang.HEADER_SELECT_WARGOAL, 3);
        this.warRole = warRole;
        this.war = war;
        this.enemyTownData = (TownData) (war.isMainAttacker(territoryData) ? war.getMainDefender() : war.getMainAttacker());
        this.returnGui = returnGui;
        open();
    }

    @Override
    public void open() {
        iterator(getLandmarks(langType), p -> returnGui.open());
        gui.open(player);
    }

    private List<GuiItem> getLandmarks(LangType langType) {

        List<GuiItem> items = new ArrayList<>();

        for(Landmark landmark : LandmarkStorage.getInstance().getLandmarkOf(enemyTownData)) {
            GuiItem item = landmark.getIcon(langType)
                    .setName(landmark.getName())
                    .setClickToAcceptMessage(Lang.GUI_GENERIC_CLICK_TO_SELECT)
                    .setAction(event -> {
                        war.addGoal(warRole, new CaptureLandmarkWarGoal(landmark));
                        returnGui.open();
                    })
                    .asGuiItem(player, langType);
            items.add(item);
        }
        return items;
    }
}
