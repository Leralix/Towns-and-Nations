package org.leralix.tan.newsletter.news;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.dataclass.PlayerData;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.enums.TownRelation;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.newsletter.NewsletterType;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.TerritoryUtil;

import java.util.function.Consumer;

import static org.leralix.lib.data.SoundEnum.MINOR_GOOD;
import static org.leralix.tan.utils.TanChatUtils.getTANString;

public class DiplomacyAcceptedNews extends Newsletter {
    private final String proposingTerritoryID;
    private final String receivingTerritoryID;
    private final TownRelation wantedRelation;
    private final boolean isRelationWorse;

    public DiplomacyAcceptedNews(String proposingTerritoryID, String receivingTerritoryID, TownRelation wantedRelation, boolean isRelationWorse) {
        super();
        this.proposingTerritoryID = proposingTerritoryID;
        this.receivingTerritoryID = receivingTerritoryID;
        this.wantedRelation = wantedRelation;
        this.isRelationWorse = isRelationWorse;
    }

    @Override
    public NewsletterType getType() {
        return NewsletterType.DIPLOMACY_PROPOSAL;
    }

    @Override
    public void broadcast(Player player) {
        SoundUtil.playSound(player, MINOR_GOOD);
        TerritoryData proposingTerritory = TerritoryUtil.getTerritory(proposingTerritoryID);
        if(proposingTerritory == null)
            return;
        TerritoryData receivingTerritory = TerritoryUtil.getTerritory(receivingTerritoryID);
        if(receivingTerritory == null)
            return;

        if(isRelationWorse){
            player.sendMessage(getTANString() + Lang.BROADCAST_RELATION_WORSEN.get(proposingTerritory.getCustomColoredName().toLegacyText(), receivingTerritory.getCustomColoredName().toLegacyText(), wantedRelation.getColoredName()));
            SoundUtil.playSound(player, SoundEnum.BAD);
        }
        else{
            player.sendMessage(getTANString() + Lang.BROADCAST_RELATION_IMPROVE.get(proposingTerritory.getCustomColoredName().toLegacyText(), receivingTerritory.getCustomColoredName().toLegacyText(), wantedRelation.getColoredName()));
            SoundUtil.playSound(player, SoundEnum.GOOD);
       }
    }

    @Override
    public GuiItem createGuiItem(Player player, Consumer<Player> onClick) {
        return null;
    }

    @Override
    public boolean shouldShowToPlayer(Player player) {
        TerritoryData proposingTerritory = TerritoryUtil.getTerritory(proposingTerritoryID);
        if(proposingTerritory == null)
            return false;
        TerritoryData receivingTerritory = TerritoryUtil.getTerritory(receivingTerritoryID);
        if(receivingTerritory == null)
            return false;
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        return receivingTerritory.isPlayerIn(playerData) || proposingTerritory.isPlayerIn(playerData);
    }
}
