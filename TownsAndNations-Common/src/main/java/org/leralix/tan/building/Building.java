package org.leralix.tan.building;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.leralix.lib.position.Vector3D;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.lang.LangType;

public abstract class Building {

    public abstract GuiItem getGuiItem(IconManager iconManager, Player player, BasicGui basicGui, LangType langType);

    public abstract Vector3D getPosition();

}
