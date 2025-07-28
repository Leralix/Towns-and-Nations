package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.war.War;
import org.leralix.tan.war.WarStorage;

import java.util.ArrayList;
import java.util.List;

public class WarsMenu extends IteratorGUI {

    private final TerritoryData territoryData;

    public WarsMenu(Player player, TerritoryData territoryData) {
        super(player, "War Menu", 4);
        this.territoryData = territoryData;
        open();
    }

    @Override
    public void open() {
        iterator(getWars(territoryData), p -> territoryData.openMainMenu(player));
        gui.open(player);
    }

    public List<GuiItem> getWars(TerritoryData territoryData) {

        List<War> wars = WarStorage.getWarsOfTerritory(territoryData);
        List<GuiItem> guiItems = new ArrayList<>();
        for(War war : wars) {
            guiItems.add(iconManager.get(war.getIcon())
                    .setName(war.getName())
                    .setAction(event -> new WarMenu(player, territoryData, war))
                    .asGuiItem(player));

        }
        return guiItems;
    }

}

