package org.leralix.tan.gui.admin;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.TownData;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.TownDataStorage;

import java.util.ArrayList;
import java.util.List;

public class AdminAssignPlayerToTown extends IteratorGUI {

    private final ITanPlayer target;

    public AdminAssignPlayerToTown(Player player, ITanPlayer target){
        super(player, Lang.HEADER_ADMIN_SET_PLAYER_TOWN, 6);
        this.target = target;
    }

    @Override
    public void open() {
        iterator(getTownsToAssign(), p -> new AdminManagePlayer(player, target));
    }

    private List<GuiItem> getTownsToAssign() {
        List<GuiItem> res = new ArrayList<>();
        for (TownData townData : TownDataStorage.getInstance().getAll().values()) {

            res.add(townData
                    .getIconWithInformations(target.getLang())
                    .setClickToAcceptMessage(Lang.ADMIN_GUI_LEFT_CLICK_TO_MANAGE_TOWN)
                    .setAction(action -> {
                        townData.addPlayer(target);
                        new AdminManagePlayer(player, target);
                    })
                    .asGuiItem(player, langType)
            );
        }
        return res;
    }
}
