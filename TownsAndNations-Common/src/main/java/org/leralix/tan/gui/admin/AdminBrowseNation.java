package org.leralix.tan.gui.admin;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.data.territory.Nation;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.lang.Lang;

import java.util.ArrayList;
import java.util.List;

public class AdminBrowseNation extends IteratorGUI {

    public AdminBrowseNation(Player player) {
        super(player, Lang.HEADER_ADMIN_NATION_MENU, 6);
        open();
    }

    @Override
    public void open() {
        iterator(getNations(), p -> new AdminMainMenu(player));
        gui.open(player);
    }

    private List<GuiItem> getNations() {
        ArrayList<GuiItem> guiItems = new ArrayList<>();

        for (Nation nationData : TownsAndNations.getPlugin().getNationStorage().getAll().values()) {
            guiItems.add(nationData.getIconWithInformations(langType)
                    .addDescription(Lang.ADMIN_GUI_REGION_DESC.get())
                    .setAction(action -> new AdminManageNation(player, nationData))
                    .asGuiItem(player, langType)
            );
        }
        return guiItems;
    }
}
