package org.leralix.tan.newsletter.news;

import dev.triumphteam.gui
.builder.item.ItemBuilder;
import dev.triumphteam.gui
.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.dataclass.PlayerData;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.HeadUtils;
import org.leralix.tan.utils.TerritoryUtil;
import org.leralix.tan.gui.PlayerGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.newsletter.NewsletterType;

import java.util.function.Consumer;

import static org.leralix.tan.utils.TanChatUtils.getTANString;

public class TownJoinRegionProposalNews extends Newsletter {
    String proposingTerritoryID;
    String receivingTerritoryID;

    public TownJoinRegionProposalNews(TerritoryData proposingTerritory, TerritoryData receivingTerritory) {
        this(proposingTerritory.getID(), receivingTerritory.getID());
    }

    public TownJoinRegionProposalNews(String proposingTerritoryID, String receivingTerritoryID) {
        super();
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
        PlayerData playerData = PlayerDataStorage.getInstance().get(player);
        if(!territoryData.isPlayerIn(playerData))
            return false;
        //TODO check if player have right to accept relation (need to add role in territory) Right now only leader can see newsletter
        return territoryData.isLeader(playerData);
    }

    @Override
    public NewsletterType getType() {
        return NewsletterType.TOWN_JOIN_REGION_PROPOSAL;
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
}
