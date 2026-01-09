package org.leralix.tan.gui.user.territory;

import org.leralix.tan.dataclass.territory.KingdomData;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;

import java.util.ArrayList;
import java.util.List;

public final class TerritoryInfoLoreUtil {

    private TerritoryInfoLoreUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static List<FilledLang> getTerritoryInfoLore(TerritoryData territoryData) {
        List<FilledLang> lore = new ArrayList<>();
        lore.add(Lang.GUI_TOWN_INFO_DESC0.get(territoryData.getDescription()));
        lore.add(Lang.GUI_TOWN_INFO_DESC1.get(territoryData.getLeaderName()));
        lore.add(Lang.GUI_TOWN_INFO_DESC2.get(Integer.toString(territoryData.getPlayerIDList().size())));
        lore.add(Lang.GUI_TOWN_INFO_DESC3.get(Integer.toString(territoryData.getNumberOfClaimedChunk())));
        if (territoryData instanceof TownData) {
            lore.add(territoryData.getOverlord()
                    .map(overlord -> Lang.GUI_TOWN_INFO_DESC5_REGION.get(overlord.getName()))
                    .orElseGet(Lang.GUI_TOWN_INFO_DESC5_NO_REGION::get));
        } else if (territoryData instanceof RegionData) {
            lore.add(territoryData.getOverlord()
                    .map(overlord -> Lang.GUI_REGION_INFO_DESC6_KINGDOM.get(overlord.getName()))
                    .orElseGet(Lang.GUI_REGION_INFO_DESC6_NO_KINGDOM::get));
        } else if (territoryData instanceof KingdomData) {
            lore.add(Lang.GUI_KINGDOM_INFO_DESC6_NO_OVERLORD.get());
        }
        return lore;
    }
}
