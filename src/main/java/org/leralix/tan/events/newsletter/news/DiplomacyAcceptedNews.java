package org.leralix.tan.events.newsletter.news;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.enums.TownRelation;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.events.newsletter.NewsletterType;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.HeadUtils;
import org.leralix.tan.utils.TerritoryUtil;

import java.util.UUID;
import java.util.function.Consumer;

import static org.leralix.lib.data.SoundEnum.MINOR_GOOD;
import static org.leralix.tan.utils.TanChatUtils.getTANString;

public class DiplomacyAcceptedNews extends Newsletter {
    private final String proposingTerritoryID;
    private final String receivingTerritoryID;
    private final TownRelation wantedRelation;
    private final boolean isRelationWorse;

    public DiplomacyAcceptedNews(UUID id, long date, String proposingTerritoryID, String receivingTerritoryID, TownRelation wantedRelation, boolean isRelationWorse) {
        super(id, date);
        this.proposingTerritoryID = proposingTerritoryID;
        this.receivingTerritoryID = receivingTerritoryID;
        this.wantedRelation = wantedRelation;
        this.isRelationWorse = isRelationWorse;
    }

    public DiplomacyAcceptedNews(String proposingTerritoryID, String receivingTerritoryID, TownRelation wantedRelation, boolean isRelationWorse) {
        super();
        this.proposingTerritoryID = proposingTerritoryID;
        this.receivingTerritoryID = receivingTerritoryID;
        this.wantedRelation = wantedRelation;
        this.isRelationWorse = isRelationWorse;
    }

    @Override
    public NewsletterType getType() {
        return NewsletterType.DIPLOMACY_ACCEPTED;
    }

    public String getProposingTerritoryID() {
        return proposingTerritoryID;
    }

    public String getReceivingTerritoryID() {
        return receivingTerritoryID;
    }

    public TownRelation getWantedRelation() {
        return wantedRelation;
    }

    public boolean isRelationWorse() {
        return isRelationWorse;
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

        TerritoryData proposingTerritory = TerritoryUtil.getTerritory(proposingTerritoryID);
        if(proposingTerritory == null)
            return null;
        TerritoryData receivingTerritory = TerritoryUtil.getTerritory(receivingTerritoryID);
        if(receivingTerritory == null)
            return null;

        ItemStack itemStack = HeadUtils.makeSkullURL(Lang.DIPLOMACY_ACCEPT_NEWSLETTER_TITLE.get(), "http://textures.minecraft.net/texture/b62c08805bd9c957da3450554a09e994042f54695db855c1c2cb47ef442e1bf6",
                Lang.BROADCAST_RELATION_WORSEN.get(proposingTerritory.getCustomColoredName().toLegacyText(), receivingTerritory.getCustomColoredName().toLegacyText(), wantedRelation.getColoredName()),
                Lang.NEWSLETTER_RIGHT_CLICK_TO_MARK_AS_READ.get());

        return ItemBuilder.from(itemStack).asGuiItem(e -> {
            e.setCancelled(true);
            if(e.isRightClick()){
                markAsRead(player);
                onClick.accept(player);
            }
        });
    }

    @Override
    public GuiItem createConcernedGuiItem(Player player, Consumer<Player> onClick) {
        return createGuiItem(player, onClick);
    }

    @Override
    public boolean shouldShowToPlayer(Player player) {
        TerritoryData proposingTerritory = TerritoryUtil.getTerritory(proposingTerritoryID);
        if(proposingTerritory == null)
            return false;
        TerritoryData receivingTerritory = TerritoryUtil.getTerritory(receivingTerritoryID);
        if(receivingTerritory == null)
            return false;
        ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(player);
        return receivingTerritory.isPlayerIn(tanPlayer) || proposingTerritory.isPlayerIn(tanPlayer);
    }

    @Override
    public void broadcastConcerned(Player player) {
        broadcast(player);
    }
}
