package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.tan.data.building.fort.Fort;
import org.leralix.tan.data.territory.TerritoryData;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.FortStorage;
import org.leralix.tan.war.War;
import org.leralix.tan.war.info.WarRole;
import org.leralix.tan.war.wargoals.CaptureFortWarGoal;

import java.util.ArrayList;
import java.util.List;

public class SelectFortForCapture extends IteratorGUI {

    private final WarRole warRole;
    private final War war;
    private final BasicGui returnGui;

    private final TerritoryData enemyTerritoryData;

    public SelectFortForCapture(Player player, TerritoryData territoryData, War war, WarRole warRole, BasicGui returnGui) {
        super(player, Lang.HEADER_SELECT_WARGOAL, 3);
        this.warRole = warRole;
        this.war = war;
        this.enemyTerritoryData = war.isMainAttacker(territoryData) ? war.getMainDefender() : war.getMainAttacker();
        this.returnGui = returnGui;
        open();
    }

    @Override
    public void open() {
        iterator(getForts(), p -> returnGui.open());
        gui.open(player);
    }

    private List<GuiItem> getForts() {

        List<GuiItem> items = new ArrayList<>();

        for(Fort fort : FortStorage.getInstance().getOwnedFort(enemyTerritoryData)) {
            items.add(iconManager.get(new ItemStack(Material.IRON_BLOCK))
                    .setName(fort.getName())
                    .setClickToAcceptMessage(Lang.GUI_GENERIC_CLICK_TO_SELECT)
                    .setAction(event -> {
                        war.addGoal(warRole, new CaptureFortWarGoal(fort));
                        returnGui.open();
                    })
                    .asGuiItem(player, langType));
        }
        return items;
    }
}
