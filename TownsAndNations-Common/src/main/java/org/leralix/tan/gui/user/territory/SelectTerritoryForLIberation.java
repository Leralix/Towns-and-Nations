package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.war.War;
import org.leralix.tan.war.legacy.WarRole;
import org.leralix.tan.war.legacy.wargoals.LiberateWarGoal;

import java.util.ArrayList;
import java.util.List;

public class SelectTerritoryForLIberation extends IteratorGUI {

    private final WarRole warRole;
    private final War war;
    private final BasicGui returnGui;

    private final TerritoryData enemyTerritory;


    public SelectTerritoryForLIberation(Player player, TerritoryData territoryData, War war, WarRole warRole, BasicGui returnGui) {
        super(player, Lang.HEADER_SELECT_WARGOAL, 3);
        this.warRole = warRole;
        this.war = war;
        this.returnGui = returnGui;
        this.enemyTerritory = war.isMainAttacker(territoryData) ? war.getMainDefender() : war.getMainAttacker();
        open();
    }

    @Override
    public void open() {
        iterator(getTerritoryToLiberate(), p -> returnGui.open());
        gui.open(player);
    }

    private List<GuiItem> getTerritoryToLiberate() {
        List<GuiItem> items = new ArrayList<>();

        for(TerritoryData territory : enemyTerritory.getVassalsInternal()) {

            if(territory.isCapital()){
                continue;
            }

            items.add(iconManager.get(territory.getIcon())
                    .setName(territory.getName())
                    .setClickToAcceptMessage(Lang.GUI_GENERIC_CLICK_TO_SELECT)
                    .setAction(action -> {
                        SoundUtil.playSound(player, SoundEnum.MINOR_GOOD);
                        war.addGoal(warRole, new LiberateWarGoal(territory));
                        returnGui.open();
                    })
                    .asGuiItem(player, langType));

        }
        return items;
    }
}
