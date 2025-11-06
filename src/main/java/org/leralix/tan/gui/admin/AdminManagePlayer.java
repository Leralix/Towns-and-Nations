package org.leralix.tan.gui.admin;

import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.lang.Lang;

public class AdminManagePlayer extends BasicGui {


    public AdminManagePlayer(Player player, ITanPlayer tanPlayer) {
        super(player, Lang.HEADER_ADMIN_PLAYER_MENU.get(player, tanPlayer.getNameStored()), 3);
        open();
    }

    @Override
    public void open() {
        
    }
}
