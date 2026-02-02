package org.leralix.tan.gui.admin;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.data.territory.TerritoryData;
import org.leralix.tan.data.territory.TownData;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.user.territory.SelectNewOwnerForTownMenu;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.utils.deprecated.GuiUtil;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.ArrayList;
import java.util.List;

public class AdminManageTown extends AdminManageTerritory {

    private final TownData townData;

    public AdminManageTown(Player player, TownData townData) {
        super(player, Lang.HEADER_ADMIN_SPECIFIC_REGION_MENU.get(townData.getName()), 3, townData);
        this.townData = townData;
        open();
    }

    @Override
    public void open() {

        gui.setItem(2, 2, getRenameTerritory());
        gui.setItem(2, 3, getChangeDescription());
        gui.setItem(2, 4, changeLeader());
        gui.setItem(2, 5, getChangeRegion());

        gui.setItem(2, 6, getDonateTerritory());
        gui.setItem(2, 7, getTransactionHistory());
        gui.setItem(2, 8, getDelete());

        gui.setItem(3, 1, GuiUtil.createBackArrow(player, p -> new AdminBrowseTown(player), langType));

        gui.open(player);
    }

    private @NotNull GuiItem getChangeRegion() {
        String name = townData.getOverlord()
                .map(TerritoryData::getName)
                .orElseGet(() -> Lang.NO_REGION.get(langType));

        List<Lang> description = new ArrayList<>();

        if (townData.haveOverlord()) {
            if (townData.isCapital())
                description.add(Lang.GUI_CANNOT_QUIT_IF_LEADER);
            else
                description.add(Lang.GUI_RIGHT_CLICK_TO_QUIT);
        }
        else {
            description.add(Lang.GUI_LEFT_CLICK_TO_SET_REGION);
        }


        return iconManager.get(IconKey.REGION_BASE_ICON)
                .setName(name)
                .setDescription()
                .setClickToAcceptMessage(description)
                .setAction(action -> {
                    if (townData.haveOverlord()) {
                        if (townData.isCapital())
                            TanChatUtils.message(player, Lang.GUI_CANNOT_QUIT_IF_LEADER.get(langType));
                        else {
                            townData.removeOverlord();
                            open();
                        }
                    } else {
                        new AdminSelectNewOverlord(player, townData);
                    }
                })
                .asGuiItem(player, langType);
    }

    private @NotNull GuiItem changeLeader() {
        return iconManager.get(IconKey.TOWN_CHANGE_OWNERSHIP_ICON)
                .setName(Lang.GUI_TOWN_SETTINGS_TRANSFER_OWNERSHIP.get(langType))
                .setDescription(
                        Lang.GUI_TOWN_SETTINGS_TRANSFER_OWNERSHIP_DESC1.get(townData.getLeaderData().getNameStored())
                )
                .setClickToAcceptMessage(Lang.GUI_REGION_CHANGE_CAPITAL_DESC2)
                .setAction(action -> new SelectNewOwnerForTownMenu(player, townData, this::open))
                .asGuiItem(player, langType);
    }

}
