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

public class DiplomacyProposalNL extends Newsletter {
    String proposingTerritoryID;
    String receivingTerritoryID;
    TownRelation wantedRelation;

    public DiplomacyProposalNL(String proposingTerritoryID, String receivingTerritoryID, TownRelation wantedRelation) {
        super();
        this.proposingTerritoryID = proposingTerritoryID;
        this.receivingTerritoryID = receivingTerritoryID;
        this.wantedRelation = wantedRelation;
    }

    @Override
    public GuiItem createGuiItem(Player player, Consumer<Player> onClick) {
        ITerritoryData proposingTerritory = TerritoryUtil.getTerritory(proposingTerritoryID);
        ITerritoryData receivingTerritory = TerritoryUtil.getTerritory(receivingTerritoryID);
        if(proposingTerritory == null || receivingTerritory == null)
            return null;

        ItemStack icon = HeadUtils.createCustomItemStack(Material.PAPER,
                Lang.NEWSLETTER_DIPLOMACY_PROPOSAL.get(proposingTerritory.getColoredName()),
                Lang.NEWSLETTER_DIPLOMACY_PROPOSAL_DESC1.get(receivingTerritory.getColoredName(), wantedRelation.getColoredName()),
                Lang.NEWSLETTER_DIPLOMACY_PROPOSAL_DESC2.get(),
                Lang.NEWSLETTER_RIGHT_CLICK_TO_MARK_AS_READ.get());
        return ItemBuilder.from(icon).asGuiItem(event -> {
            event.setCancelled(true);
            if(event.isLeftClick())
                PlayerGUI.openProposalMenu(player, receivingTerritory, 0, p -> receivingTerritory.openMainMenu(player));
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
        PlayerData playerData = PlayerDataStorage.get(player);
        if(!territoryData.havePlayer(playerData))
            return false;
        //TODO check if player have right to accept relation (need to add role in territory) Right now only leader can see newsletter
        territoryData.isLeader(playerData);
        return true;
    }

    @Override
    public NewsletterType getType() {
        return NewsletterType.DIPLOMACY_PROPOSAL;
    }
}
