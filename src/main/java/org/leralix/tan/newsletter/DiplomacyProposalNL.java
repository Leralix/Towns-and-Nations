package org.leralix.tan.newsletter;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.tan.dataclass.PlayerData;
import org.leralix.tan.dataclass.territory.ITerritoryData;
import org.leralix.tan.enums.TownRelation;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.TerritoryUtil;

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
    public GuiItem createGuiItem(Player player) {
        ItemStack icon = new ItemStack(Material.PAPER);
        GuiItem guiItem = ItemBuilder.from(icon).setName("Diplomacy proposal").asGuiItem(event -> {
            event.setCancelled(true);
        });

        return guiItem;
    }

    @Override
    public boolean shouldShowToPlayer(Player player) {
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
