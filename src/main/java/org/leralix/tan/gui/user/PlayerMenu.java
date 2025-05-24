package org.leralix.tan.gui.user;

import org.bukkit.entity.Player;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.lang.Lang;

public class PlayerMenu extends BasicGui {

    protected PlayerMenu(Player player) {
        super(player, Lang.HEADER_PLAYER_PROFILE, 3);
    }

    @Override
    public void open(){




        gui.open(player);
    }


}
