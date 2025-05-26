package org.leralix.tan.gui.user;

import org.bukkit.entity.Player;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.lang.Lang;

public class NewsletterMenu extends IteratorGUI {

    public NewsletterMenu(Player player) {
        super(player, Lang.HEADER_NEWSLETTER, 6);
    }

    @Override
    public void open() {
        // Implement the logic to open the newsletter menu
        // This could include displaying available newsletters, subscription options, etc.
        gui.open(player);
    }
}
