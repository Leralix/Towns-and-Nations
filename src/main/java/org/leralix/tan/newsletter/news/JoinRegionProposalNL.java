package org.leralix.tan.newsletter.news;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.tan.dataclass.PlayerData;
import org.leralix.tan.dataclass.territory.ITerritoryData;
import org.leralix.tan.enums.TownRelation;
import org.leralix.tan.gui.PlayerGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.newsletter.NewsletterScope;
import org.leralix.tan.newsletter.NewsletterType;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.HeadUtils;
import org.leralix.tan.utils.TerritoryUtil;

import java.util.function.Consumer;

public class JoinRegionProposalNL extends Newsletter {
    String proposingTerritoryID;
    String receivingTerritoryID;

    public JoinRegionProposalNL(ITerritoryData proposingTerritory, ITerritoryData receivingTerritory) {
        this(proposingTerritory.getID(), receivingTerritory.getID());
    }

    public JoinRegionProposalNL(String proposingTerritoryID, String receivingTerritoryID) {
        super();
        this.proposingTerritoryID = proposingTerritoryID;
        this.receivingTerritoryID = receivingTerritoryID;
    }

    @Override
    public GuiItem createGuiItem(Player player, Consumer<Player> onClick) {
        ITerritoryData proposingTerritory = TerritoryUtil.getTerritory(proposingTerritoryID);
        ITerritoryData receivingTerritory = TerritoryUtil.getTerritory(receivingTerritoryID);
        if(proposingTerritory == null || receivingTerritory == null)
            return null;

        ItemStack icon = HeadUtils.createCustomItemStack(Material.GOLDEN_HELMET,
                Lang.NEWSLETTER_JOIN_REGION_PROPOSAL.get(),
                Lang.NEWSLETTER_JOIN_REGION_PROPOSAL_DESC1.get(proposingTerritory.getColoredName(), receivingTerritory.getColoredName()),
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
    public boolean shouldShowToPlayer(Player player, NewsletterScope scope) {
        if(isRead(player) && scope == NewsletterScope.SHOW_ONLY_UNREAD)
            return false;
        ITerritoryData territoryData = TerritoryUtil.getTerritory(receivingTerritoryID);
        if(territoryData == null)
            return false;
        PlayerData playerData = PlayerDataStorage.get(player);
        if(!territoryData.havePlayer(playerData))
            return false;
        //TODO check if player have right to accept relation (need to add role in territory) Right now only leader can see newsletter
        return territoryData.isLeader(playerData);
    }

    @Override
    public NewsletterType getType() {
        return NewsletterType.JOIN_REGION_PROPOSAL;
    }

    public String getProposingTerritoryID() {
        return proposingTerritoryID;
    }
    public String getReceivingTerritoryID() {
        return receivingTerritoryID;
    }
}
