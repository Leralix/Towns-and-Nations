package org.leralix.tan.newsletter;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
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
    }

    @Override
    public GuiItem createGuiItem(Player player) {
        return null;
    }

    @Override
    public boolean shouldShowToPlayer(Player player) {
        ITerritoryData territoryData = TerritoryUtil.getTerritory(proposingTerritoryID);
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
