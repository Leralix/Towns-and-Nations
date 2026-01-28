package org.leralix.tan.data.building;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.lang.LangType;
import org.tan.api.interfaces.TanBuilding;

public abstract class Building implements TanBuilding {

    public abstract GuiItem getGuiItem(IconManager iconManager, Player player, BasicGui basicGui, LangType langType);

}
