package org.leralix.tan.gui.admin;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.gui.cosmetic.type.IconBuilder;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.RegionDataStorage;

import java.util.ArrayList;
import java.util.List;

public class AdminSelectNewOverlord extends IteratorGUI {
    private final TownData townData;

    public AdminSelectNewOverlord(Player player, TownData townData) {
        super(player, Lang.HEADER_ADMIN_CHANGE_OVERLORD.get(player, townData.getName()), 6);
        this.townData = townData;
        open();
    }

    @Override
    public void open() {
        iterator(getRegions(), p -> new AdminManageTown(player, townData));

        gui.open(player);
    }

    private List<GuiItem> getRegions() {
        List<GuiItem> guiItems = new ArrayList<>();

        for (TerritoryData potentialOverlord : RegionDataStorage.getInstance().getAll().values()) {
            IconBuilder potentialOverlordIcon = potentialOverlord.getIconWithInformations(tanPlayer.getLang());
            potentialOverlordIcon.addDescription(Lang.GUI_GENERIC_CLICK_TO_SELECT.get());

            potentialOverlordIcon.setAction(action -> {
                townData.setOverlord(potentialOverlord);
                new AdminManageTown(player, townData);
            });
            guiItems.add(potentialOverlordIcon.asGuiItem(player, langType));
        }
        return guiItems;
    }
}
