package org.leralix.tan.newsletter.news;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.enums.RolePermission;
import org.leralix.tan.gui.legacy.PlayerGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.newsletter.NewsletterType;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.HeadUtils;
import org.leralix.tan.utils.TerritoryUtil;

import java.util.UUID;
import java.util.function.Consumer;

import static org.leralix.tan.utils.TanChatUtils.getTANString;

public class TerritoryVassalProposalNews extends Newsletter {
    String proposingTerritoryID;
    String receivingTerritoryID;

    public TerritoryVassalProposalNews(TerritoryData proposingTerritory, TerritoryData receivingTerritory) {
        this(proposingTerritory.getID(), receivingTerritory.getID());
    }

    public TerritoryVassalProposalNews(String proposingTerritoryID, String receivingTerritoryID) {
        super();
        this.proposingTerritoryID = proposingTerritoryID;
        this.receivingTerritoryID = receivingTerritoryID;
    }

    public TerritoryVassalProposalNews(UUID id, long date, String proposingTerritoryID, String receivingTerritoryID) {
        super(id, date);
        this.proposingTerritoryID = proposingTerritoryID;
        this.receivingTerritoryID = receivingTerritoryID;
    }

    @Override
    public GuiItem createGuiItem(Player player, Consumer<Player> onClick) {
        TerritoryData proposingTerritory = TerritoryUtil.getTerritory(proposingTerritoryID);
        TerritoryData receivingTerritory = TerritoryUtil.getTerritory(receivingTerritoryID);
        if(proposingTerritory == null || receivingTerritory == null)
            return null;

        ItemStack icon = HeadUtils.createCustomItemStack(Material.GOLDEN_HELMET,
                Lang.NEWSLETTER_JOIN_REGION_PROPOSAL.get(),
                Lang.NEWSLETTER_JOIN_REGION_PROPOSAL_DESC1.get(proposingTerritory.getBaseColoredName(), receivingTerritory.getBaseColoredName()),
                Lang.NEWSLETTER_RIGHT_CLICK_TO_MARK_AS_READ.get());


        return ItemBuilder.from(icon).asGuiItem(event -> {
            event.setCancelled(true);
            if(event.isRightClick()){
                markAsRead(player);
                onClick.accept(player);
            }
        });
    }

    @Override
    public GuiItem createConcernedGuiItem(Player player, Consumer<Player> onClick) {
        TerritoryData proposingTerritory = TerritoryUtil.getTerritory(proposingTerritoryID);
        TerritoryData receivingTerritory = TerritoryUtil.getTerritory(receivingTerritoryID);
        if(proposingTerritory == null || receivingTerritory == null)
            return null;

        ItemStack icon = HeadUtils.createCustomItemStack(Material.GOLDEN_HELMET,
                Lang.NEWSLETTER_JOIN_REGION_PROPOSAL.get(),
                Lang.NEWSLETTER_JOIN_REGION_PROPOSAL_DESC1.get(proposingTerritory.getBaseColoredName(), receivingTerritory.getBaseColoredName()),
                Lang.NEWSLETTER_JOIN_REGION_PROPOSAL_DESC2.get(),
                Lang.NEWSLETTER_RIGHT_CLICK_TO_MARK_AS_READ.get());


        return ItemBuilder.from(icon).asGuiItem(event -> {
            event.setCancelled(true);
            if(event.isLeftClick())
                PlayerGUI.openChooseOverlordMenu(player, receivingTerritory, 0);
            if(event.isRightClick()){
                markAsRead(player);
                onClick.accept(player);
            }
        });
    }

    @Override
    public boolean shouldShowToPlayer(Player player) {
        TerritoryData territoryData = TerritoryUtil.getTerritory(receivingTerritoryID);
        if(territoryData == null)
            return false;
        ITanPlayer ITanPlayer = PlayerDataStorage.getInstance().get(player);
        if(!territoryData.isPlayerIn(ITanPlayer))
            return false;
        return territoryData.doesPlayerHavePermission(ITanPlayer, RolePermission.TOWN_ADMINISTRATOR);
    }

    @Override
    public NewsletterType getType() {
        return NewsletterType.TERRITORY_VASSAL_PROPOSAL;
    }

    @Override
    public void broadcast(Player player) {
        TerritoryData proposingTerritory = TerritoryUtil.getTerritory(proposingTerritoryID);
        if(proposingTerritory == null)
            return;
        TerritoryData receivingTerritory = TerritoryUtil.getTerritory(receivingTerritoryID);
        if(receivingTerritory == null)
            return;
        player.sendMessage(getTANString() + Lang.TOWN_JOIN_REGION_PROPOSAL_NEWSLETTER.get(proposingTerritory.getBaseColoredName(), receivingTerritory.getBaseColoredName()));
        SoundUtil.playSound(player, SoundEnum.MINOR_BAD);
    }

    public String getProposingTerritoryID() {
        return proposingTerritoryID;
    }

    public String getReceivingTerritoryID() {
        return receivingTerritoryID;
    }

    @Override
    public void broadcastConcerned(Player player) {
        broadcast(player);
    }
}
