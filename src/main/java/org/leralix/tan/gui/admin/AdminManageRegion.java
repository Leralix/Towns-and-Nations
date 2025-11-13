package org.leralix.tan.gui.admin;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.lang.Lang;

public class AdminManageRegion extends AdminManageTerritory {


    public AdminManageRegion(Player player, RegionData regionData) {
        super(player, Lang.HEADER_ADMIN_SPECIFIC_REGION_MENU.get(regionData.getName()), 3, regionData);
        open();
    }

    @Override
    public void open() {

        gui.setItem(2, 2, getRenameTerritory());
        gui.setItem(2, 3, getChangeDescription());
        gui.setItem(2, 4, changeLeader());

        gui.setItem(2, 6, getTransactionHistory());
        gui.setItem(2, 7, getDelete());

        gui.open(player);
    }

    private @NotNull GuiItem changeLeader() {
        //TODO : merge leader system between town and region
        return null;
    }

}
