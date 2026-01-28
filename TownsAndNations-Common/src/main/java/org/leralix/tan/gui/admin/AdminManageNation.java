package org.leralix.tan.gui.admin;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.leralix.tan.data.territory.NationData;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.utils.deprecated.GuiUtil;

public class AdminManageNation extends AdminManageTerritory {

    private final NationData nationData;

    public AdminManageNation(Player player, NationData nationData) {
        super(player, Lang.HEADER_ADMIN_SPECIFIC_NATION_MENU.get(nationData.getName()), 3, nationData);
        this.nationData = nationData;
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

        gui.setItem(3, 1, GuiUtil.createBackArrow(player, p -> new AdminBrowseNation(player)));

        gui.open(player);
    }

    private GuiItem changeLeader() {
        return iconManager.get(IconKey.REGION_CHANGE_OWNERSHIP_ICON)
                .setName(Lang.GUI_REGION_CHANGE_CAPITAL.get(langType))
                .setDescription(
                        Lang.GUI_REGION_CHANGE_CAPITAL_DESC1.get(nationData.getCapital() != null ? nationData.getCapital().getColoredName() : "None")
                )
                .setClickToAcceptMessage(Lang.GUI_REGION_CHANGE_CAPITAL_DESC2)
                .setAction(action -> {
                    action.setCancelled(true);
                    new NationChangeOwnership(player, nationData);
                })
                .asGuiItem(player, langType);
    }
}
