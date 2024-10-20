package org.leralix.tan.newsletter;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;

import java.util.Date;

public abstract class Newsletter {

    double date;



    protected Newsletter() {
        this.date = new Date().getTime();
    }

    public abstract GuiItem createGuiItem(Player player);

    public double getDate() {
        return date;
    }




}
