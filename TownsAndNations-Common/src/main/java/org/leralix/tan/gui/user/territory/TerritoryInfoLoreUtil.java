package org.leralix.tan.gui.user.territory;

import org.leralix.tan.data.territory.Nation;
import org.leralix.tan.data.territory.Region;
import org.leralix.tan.data.territory.Territory;
import org.leralix.tan.data.territory.Town;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;

import java.util.ArrayList;
import java.util.List;

public final class TerritoryInfoLoreUtil {

    private TerritoryInfoLoreUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static List<FilledLang> getTerritoryInfoLore(Territory territoryData) {
        List<FilledLang> lore = new ArrayList<>();
        lore.add(Lang.GUI_TOWN_INFO_DESC0.get(territoryData.getDescription()));
        lore.add(Lang.GUI_TOWN_INFO_DESC1.get(territoryData.getLeaderName()));
        lore.add(Lang.GUI_TOWN_INFO_DESC2.get(Integer.toString(territoryData.getPlayerIDList().size())));
        lore.add(Lang.GUI_TOWN_INFO_DESC3.get(Integer.toString(territoryData.getNumberOfClaimedChunk())));
        if (territoryData instanceof Town) {
            lore.add(territoryData.getOverlordInternal()
                    .map(overlord -> Lang.GUI_TOWN_INFO_DESC5_REGION.get(overlord.getName()))
                    .orElseGet(Lang.GUI_TOWN_INFO_DESC5_NO_REGION::get));
        } else if (territoryData instanceof Region) {
            lore.add(territoryData.getOverlordInternal()
                    .map(overlord -> Lang.GUI_REGION_INFO_DESC6_NATION.get(overlord.getName()))
                    .orElseGet(Lang.GUI_REGION_INFO_DESC6_NO_NATION::get));
        } else if (territoryData instanceof Nation) {
            lore.add(Lang.GUI_NATION_INFO_DESC6_NO_OVERLORD.get());
        }
        return lore;
    }
}
