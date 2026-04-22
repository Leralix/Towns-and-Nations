package org.leralix.tan.gui.admin;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.data.territory.Region;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.user.territory.RegionChangeOwnership;
import org.leralix.tan.lang.Lang;

public class AdminManageRegion extends AdminManageTerritory {

    private final Region regionData;

    public AdminManageRegion(Player player, Region regionData) {
        super(player, Lang.HEADER_ADMIN_SPECIFIC_REGION_MENU.get(regionData.getName()), 3, regionData);
        this.regionData = regionData;
        open();
    }

    @Override
    public void open() {

        gui.setItem(2, 2, getRenameTerritory());
        gui.setItem(2, 3, getChangeDescription());
        gui.setItem(2, 4, changeLeader());

        gui.setItem(2, 6, getDonateTerritory());
        gui.setItem(2, 7, getTransactionHistory());
        gui.setItem(2, 8, getDelete());

        gui.setItem(3, 1, createBackArrow(player, p -> new AdminBrowseRegion(player), langType));

        gui.open(player);
    }

    private @NotNull GuiItem changeLeader() {
        return iconManager.get(IconKey.REGION_CHANGE_OWNERSHIP_ICON)
                .setName(Lang.GUI_REGION_CHANGE_CAPITAL.get(langType))
                .setDescription(
                        Lang.GUI_REGION_CHANGE_CAPITAL_DESC1.get(regionData.getCapital().getColoredName())
                )
                .setClickToAcceptMessage(Lang.GUI_REGION_CHANGE_CAPITAL_DESC2)
                    .setAction(action -> new RegionChangeOwnership(player, regionData, this))
                .asGuiItem(player, langType);
    }

}
