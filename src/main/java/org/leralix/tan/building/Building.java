package org.leralix.tan.building;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.gui.cosmetic.IconManager;

public abstract class Building {

    public abstract GuiItem getGuiItem(IconManager iconManager, Player player, TerritoryData territoryData);

}
