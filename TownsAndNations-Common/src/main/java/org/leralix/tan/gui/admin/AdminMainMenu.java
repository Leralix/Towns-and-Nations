package org.leralix.tan.gui.admin;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.leralix.tan.gui.BasicGui;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.utils.deprecated.GuiUtil;

public class AdminMainMenu extends BasicGui {

    public AdminMainMenu(Player player) {
        super(player, Lang.HEADER_ADMIN_MAIN_MENU.get(), 3);
        open();
    }

    @Override
    public void open() {

        gui.setItem(2, 2, getNationButton());
        gui.setItem(2, 3, getRegionButton());
        gui.setItem(2, 4, getTownButton());
        gui.setItem(2, 7, getLandmarkButton());
        gui.setItem(2, 8, getWarButton());

        gui.setItem(3, 1, GuiUtil.createBackArrow(player, p -> player.closeInventory()));

        gui.open(player);
    }

    private GuiItem getWarButton() {
        return iconManager.get(IconKey.TERRITORY_WAR_ICON)
                .setName(Lang.ADMIN_GUI_WAR_ICON.get(langType))
                .setAction(action -> new AdminWarMenu(player))
                .asGuiItem(player, langType);
    }

    private GuiItem getNationButton() {
        if (!org.leralix.tan.utils.constants.Constants.enableNation()) {
            return iconManager.get(IconKey.NATION_BASE_ICON)
                    .setName(Lang.GUI_KINGDOM_ICON.get(langType))
                    .setDescription(Lang.GUI_WARNING_STILL_IN_DEV.get())
                    .asGuiItem(player, langType);
        }
        return iconManager.get(IconKey.NATION_BASE_ICON)
                .setName(Lang.GUI_KINGDOM_ICON.get(langType))
                .setAction(action -> new AdminBrowseNation(player))
                .asGuiItem(player, langType);
    }

    private GuiItem getLandmarkButton() {
        return iconManager.get(IconKey.TOWN_LANDMARKS_ICON)
                .setName(Lang.ADMIN_GUI_LANDMARK_ICON.get(langType))
                .setAction(action -> new AdminLandmarksMenu(player))
                .asGuiItem(player, langType);
    }

    private GuiItem getTownButton() {
        return iconManager.get(IconKey.TOWN_BASE_ICON)
                .setName(Lang.GUI_TOWN_ICON.get(langType))
                .setAction(action -> new AdminBrowseTown(player))
                .asGuiItem(player, langType);
    }

    private GuiItem getRegionButton() {
        return iconManager.get(IconKey.REGION_BASE_ICON)
                .setName(Lang.GUI_REGION_ICON.get(langType))
                .setAction(action -> new AdminBrowseRegion(player))
                .asGuiItem(player, langType);
    }
}
