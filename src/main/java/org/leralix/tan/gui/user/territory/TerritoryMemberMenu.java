package org.leralix.tan.gui.user.territory;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.utils.GuiUtil;

import java.util.List;

public class TerritoryMemberMenu extends IteratorGUI {

    private final TerritoryData territoryData;

    public TerritoryMemberMenu(Player player, TerritoryData territoryData) {
        super(player, Lang.HEADER_TOWN_MEMBERS, 6);
        this.territoryData = territoryData;
    }


    @Override
    public void open() {
        GuiUtil.createIterator(gui, getMemberList(), page, player,
                p -> territoryData.openMainMenu(player),
                p -> nextPage(),
                p -> previousPage(),
                Material.LIME_STAINED_GLASS_PANE
        );

        gui.setItem(6,4, getManageRankButton());
        if(territoryData instanceof TownData townData) {
            gui.setItem(6,5, getManageApplicationsButton(townData));
        }
        gui.open(player);
    }

    private List<GuiItem> getMemberList() {
        return territoryData.getOrderedMemberList(tanPlayer);
    }

    private GuiItem getManageRankButton() {
        return IconManager.getInstance().get(IconKey.MANAGE_RANKS_ICON)
                .setName(Lang.GUI_TOWN_MEMBERS_MANAGE_ROLES.get(tanPlayer))
                .setDescription(Lang.GUI_GENERIC_CLICK_TO_OPEN.get(tanPlayer))
                .setAction(p -> new TerritoryRanksMenu(player, territoryData).open())
                .asGuiItem(player);
    }

    private GuiItem getManageApplicationsButton(TownData townData) {
        return IconManager.getInstance().get(IconKey.MANAGE_APPLICATIONS_ICON)
                .setName(Lang.GUI_TOWN_MEMBERS_MANAGE_APPLICATION.get(tanPlayer))
                .setDescription(
                        Lang.GUI_TOWN_MEMBERS_MANAGE_APPLICATION_DESC1.get(tanPlayer, townData.getPlayerJoinRequestSet().size()),
                        Lang.GUI_GENERIC_CLICK_TO_OPEN.get(tanPlayer))
                .setAction(p -> new PlayerApplicationMenu(player, townData).open())
                .asGuiItem(player);
    }
}
