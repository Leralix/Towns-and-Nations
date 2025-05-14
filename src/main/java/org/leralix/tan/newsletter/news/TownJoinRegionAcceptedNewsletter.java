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

public class TownJoinRegionAcceptedNewsletter extends Newsletter {

    private final String proposingTerritoryID;
    private final String receivingTerritoryID;

    public TownJoinRegionAcceptedNewsletter(String proposingTerritoryID, String receivingTerritoryID) {
        super();
        this.proposingTerritoryID = proposingTerritoryID;
        this.receivingTerritoryID = receivingTerritoryID;
    }

    @Override
    public GuiItem createGuiItem(Player player, Consumer<Player> onClick) {
        return null;
    }

    @Override
    public boolean shouldShowToPlayer(Player player) {
        TerritoryData proposingTerritory = TerritoryUtil.getTerritory(proposingTerritoryID);
        if (proposingTerritory == null)
            return false;
        TerritoryData receivingTerritory = TerritoryUtil.getTerritory(receivingTerritoryID);
        if (receivingTerritory == null)
            return false;
        return proposingTerritory.isPlayerIn(player) || receivingTerritory.isPlayerIn(player);
    }

    @Override
    public NewsletterType getType() {
        return NewsletterType.TOWN_JOIN_REGION_ACCEPTED;
    }

    @Override
    public void broadcast(Player player) {
        TerritoryData proposingTerritory = TerritoryUtil.getTerritory(proposingTerritoryID);
        if (proposingTerritory == null)
            return;
        TerritoryData receivingTerritory = TerritoryUtil.getTerritory(receivingTerritoryID);
        if (receivingTerritory == null)
            return;

        player.sendMessage(getTANString() + Lang.TOWN_JOIN_REGION_PROPOSAL_NEWSLETTER.get(proposingTerritory.getCustomColoredName(), receivingTerritory.getCustomColoredName()));
        SoundUtil.playSound(player, SoundEnum.MINOR_GOOD);
    }
}
