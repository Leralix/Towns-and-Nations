package org.leralix.tan.gui.admin;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.leralix.tan.data.territory.TownData;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.TownDataStorage;

import java.util.ArrayList;
import java.util.List;

public class AdminBrowseTown extends IteratorGUI {


    public AdminBrowseTown(Player player) {
        super(player, Lang.HEADER_ADMIN_TOWN_MENU, 6);
        open();
    }

    @Override
    public void open() {
        iterator(getTowns(), p -> new AdminMainMenu(player));
        gui.open(player);
    }

    private List<GuiItem> getTowns() {
        ArrayList<GuiItem> guiItems = new ArrayList<>();
        for (TownData townData : TownDataStorage.getInstance().getAll().values()) {

            guiItems.add(
                    townData.getIconWithInformations(tanPlayer.getLang())
                            .setClickToAcceptMessage(Lang.ADMIN_GUI_LEFT_CLICK_TO_MANAGE_TOWN)
                            .setAction(action -> new AdminManageTown(player, townData))
                            .asGuiItem(player, langType)
            );
        }
        return guiItems;
    }
}
