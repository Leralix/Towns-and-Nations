package org.leralix.tan.newsletter.news;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.newsletter.NewsletterType;
import org.leralix.tan.utils.TerritoryUtil;

import java.util.function.Consumer;

import static org.leralix.tan.utils.TanChatUtils.getTANString;

public class TownLeaveRegionNewsletter extends Newsletter {

    private final String leavingTownID;
    private final String regionID;

    public TownLeaveRegionNewsletter(String regionID, String leavingTownID) {
        super();
        this.regionID = regionID;
        this.leavingTownID = leavingTownID;
    }

    @Override
    public GuiItem createGuiItem(Player player, Consumer<Player> onClick) {
        return null;
    }

    @Override
    public boolean shouldShowToPlayer(Player player) {
        TerritoryData leavingTown = TerritoryUtil.getTerritory(leavingTownID);
        if (leavingTown == null)
            return false;
        TerritoryData region = TerritoryUtil.getTerritory(regionID);
        if (region == null)
            return false;
        return leavingTown.isPlayerIn(player) || region.isPlayerIn(player);
    }

    @Override
    public NewsletterType getType() {
        return NewsletterType.TOWN_LEAVE_REGION;
    }

    @Override
    public void broadcast(Player player) {
        TerritoryData leavingTown = TerritoryUtil.getTerritory(leavingTownID);
        if (leavingTown == null)
            return;
        TerritoryData region = TerritoryUtil.getTerritory(regionID);
        if (region == null)
            return;

        player.sendMessage(getTANString() + Lang.TOWN_LEAVE_REGION_NEWSLETTER.get(leavingTown.getBaseColoredName(), region.getBaseColoredName()));
        SoundUtil.playSound(player, SoundEnum.MINOR_GOOD);
    }
}
