package org.leralix.tan.gui.admin;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.NationData;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.utils.file.FileUtil;

import java.util.ArrayList;
import java.util.List;

public class NationChangeOwnership extends IteratorGUI {

    private final NationData nationData;

    public NationChangeOwnership(Player player, NationData nationData){
        super(player, Lang.HEADER_CHANGE_OWNERSHIP, 6);
        this.nationData = nationData;
        open();
    }

    @Override
    public void open() {
        iterator(getCandidates(), p -> new AdminManageNation(player, nationData));
        gui.open(player);
    }

    private List<GuiItem> getCandidates() {
        ArrayList<GuiItem> guiItems = new ArrayList<>();
        for (org.leralix.tan.dataclass.territory.TerritoryData territoryData : nationData.getVassalsInternal()) {
            if (territoryData instanceof RegionData regionData) {
                ITanPlayer regionLeader = regionData.getLeaderData();
                if (regionLeader != null) {
                    guiItems.add(
                            iconManager.get(regionLeader.getOfflinePlayer())
                                    .setName(regionLeader.getNameStored())
                                    .setAction(action -> {
                                        action.setCancelled(true);
                                        new org.leralix.tan.gui.common.ConfirmMenu(
                                                player,
                                                Lang.GUI_CONFIRM_CHANGE_LEADER.get(regionLeader.getNameStored()),
                                                () -> {
                                                    FileUtil.addLineToHistory(Lang.REGION_DELETED_NEWSLETTER.get(player.getName(), nationData.getName()));
                                                    nationData.setLeaderID(regionLeader.getID());
                                                },
                                                this::open
                                        );
                                    })
                                    .asGuiItem(player, langType)
                    );
                }
            }
        }
        return guiItems;
    }
}
