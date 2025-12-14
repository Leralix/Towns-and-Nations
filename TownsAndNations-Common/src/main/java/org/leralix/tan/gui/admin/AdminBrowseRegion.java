package org.leralix.tan.gui.admin;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.RegionDataStorage;

import java.util.ArrayList;
import java.util.List;

public class AdminBrowseRegion extends IteratorGUI {


    public AdminBrowseRegion(Player player) {
        super(player, Lang.HEADER_ADMIN_REGION_MENU, 6);
        open();
    }

    @Override
    public void open() {
        iterator(getRegions(), p -> new AdminMainMenu(player));
        gui.open(player);
    }

    private List<GuiItem> getRegions() {
        ArrayList<GuiItem> guiItems = new ArrayList<>();

        for (RegionData regionData : RegionDataStorage.getInstance().getAll().values()) {

            guiItems.add(regionData.getIconWithInformations(langType)
                    .addDescription(Lang.ADMIN_GUI_REGION_DESC.get())
                    .setAction(action -> new AdminManageRegion(player, regionData))
                    .asGuiItem(player, langType)
            );
        }
        return guiItems;
    }
}
