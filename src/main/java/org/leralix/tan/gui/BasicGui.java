package org.leralix.tan.gui;

import dev.triumphteam.gui.guis.Gui;
import org.bukkit.entity.Player;
import org.leralix.tan.utils.GuiUtil;

public abstract class BasicGui {

    protected final Gui gui;
    protected final Player player;

    protected BasicGui(Player player, String title, int rows){
        gui = GuiUtil.createChestGui(title, rows);
        this.player = player;
    }

    public void open(){
        gui.open(player);
    }
}
