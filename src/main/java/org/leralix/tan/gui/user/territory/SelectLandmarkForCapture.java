package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.Landmark;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.stored.LandmarkStorage;
import org.leralix.tan.war.War;
import org.leralix.tan.war.legacy.WarRole;
import org.leralix.tan.war.legacy.wargoals.CaptureLandmarkWarGoal;

import java.util.ArrayList;
import java.util.List;

public class SelectLandmarkForCapture extends IteratorGUI {

    private final WarRole warRole;
    private final TerritoryData territoryData;
    private final War war;

    private final TownData enemyTownData;


    public SelectLandmarkForCapture(Player player, TerritoryData territoryData, War war, WarRole warRole) {
        super(player, Lang.HEADER_SELECT_WARGOAL.get(player), 3);
        this.warRole = warRole;
        this.territoryData = territoryData;
        this.war = war;
        this.enemyTownData = (TownData) (war.isMainAttacker(territoryData) ? war.getMainDefender() : war.getMainAttacker());
        open();
    }

    @Override
    public void open() {
        iterator(getLandmarks(langType), p -> new ChooseWarGoal(player, territoryData, war, warRole));
        gui.open(player);
    }

    private List<GuiItem> getLandmarks(LangType langType) {

        List<GuiItem> items = new ArrayList<>();

        for(Landmark landmark : LandmarkStorage.getInstance().getLandmarkOf(enemyTownData)) {

            GuiItem item = iconManager.get(landmark.getIcon(langType))
                    .setName(landmark.getName())
                    .setClickToAcceptMessage(Lang.GUI_GENERIC_CLICK_TO_SELECT)
                    .setAction(event -> {
                        war.addGoal(warRole, new CaptureLandmarkWarGoal(landmark));
                        new SelectWarGoals(player, territoryData, war, warRole);
                    })
                    .asGuiItem(player);
            items.add(item);
        }
        return items;
    }
}
