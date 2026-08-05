package org.leralix.tan.gui.admin;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.data.territory.Region;
import org.leralix.tan.data.territory.Territory;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.user.territory.RegionChangeOwnership;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.ArrayList;
import java.util.List;

public class AdminManageRegion extends AdminManageTerritory {

    private final Region regionData;

    public AdminManageRegion(Player player, Region regionData) {
        super(player, Lang.HEADER_ADMIN_SPECIFIC_REGION_MENU.get(regionData.getName()), 4, regionData);
        this.regionData = regionData;
        open();
    }

    @Override
    public void open() {
        gui.setItem(2, 2, getRenameTerritory());
        gui.setItem(2, 3, getChangeDescription());
        gui.setItem(2, 4, changeLeader());

        gui.setItem(2, 5, getChangeNation());

        gui.setItem(2, 6, getDonateTerritory());
        gui.setItem(2, 7, getTransactionHistory());
        gui.setItem(2, 8, getDelete());

        gui.setItem(3, 2, getAdminUpgrade());

        gui.setItem(4, 1, createBackArrow(player, p -> new AdminBrowseRegion(player), langType));

        gui.open(player);
    }

    private @NotNull GuiItem getChangeNation() {
        String name = regionData.getOverlordInternal()
                .map(Territory::getName)
                .orElseGet(() -> Lang.NO_REGION.get(langType));

        List<Lang> description = new ArrayList<>();

        if (regionData.haveOverlord()) {
            if (regionData.isCapital())
                description.add(Lang.GUI_CANNOT_QUIT_IF_LEADER);
            else
                description.add(Lang.GUI_RIGHT_CLICK_TO_QUIT);
        } else {
            description.add(Lang.GUI_LEFT_CLICK_TO_SET_REGION);
        }


        return iconManager.get(IconKey.REGION_BASE_ICON)
                .setName(name)
                .setDescription()
                .setClickToAcceptMessage(description)
                .setAction(action -> {
                    if (regionData.haveOverlord()) {
                        if (regionData.isCapital())
                            TanChatUtils.message(player, Lang.GUI_CANNOT_QUIT_IF_LEADER.get(langType));
                        else {
                            regionData.removeOverlord();
                            open();
                        }
                    } else {
                        new AdminSelectNewOverlord(player, regionData, this);
                    }
                })
                .asGuiItem(player, langType);
    }

    private @NotNull GuiItem changeLeader() {
        return iconManager.get(IconKey.REGION_CHANGE_OWNERSHIP_ICON)
                .setName(Lang.GUI_REGION_CHANGE_CAPITAL.get(langType))
                .setDescription(
                        Lang.GUI_REGION_CHANGE_CAPITAL_DESC1.get(regionData.getCapital().getColoredName())
                )
                .setClickToAcceptMessage(Lang.GUI_GENERIC_CLICK_TO_MODIFY)
                .setAction(action -> new RegionChangeOwnership(player, regionData, this))
                .asGuiItem(player, langType);
    }

}
