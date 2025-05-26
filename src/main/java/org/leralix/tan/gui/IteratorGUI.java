package org.leralix.tan.gui;

import org.bukkit.entity.Player;
import org.leralix.tan.lang.Lang;

public abstract class IteratorGUI extends BasicGui {

    protected int page;

    protected IteratorGUI(Player player, Lang title, int rows) {
        super(player, title, rows);
        this.page = 0;
    }

    protected IteratorGUI(Player player, String title, int rows) {
        super(player, title, rows);
        this.page = 0;
    }

    protected void previousPage() {
        page--;
        open();
    }

    protected void nextPage() {
        page++;
        open();
    }
}
