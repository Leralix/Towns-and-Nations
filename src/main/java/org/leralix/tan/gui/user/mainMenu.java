package org.leralix.tan.gui.user;

import org.bukkit.entity.Player;
import org.leralix.tan.gui.BasicGui;

public class mainMenu extends BasicGui {


    protected mainMenu(Player player, String title, int rows) {
        super(player, title, rows);
        gui.setDefaultClickAction(event -> event.setCancelled(true));
        gui.setDragAction(inventoryDragEvent -> inventoryDragEvent.setCancelled(true));
    }

    public void open() {



        gui.open(player);
    }
}
