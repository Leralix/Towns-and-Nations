package org.leralix.tan.data.territory.relation;

import org.leralix.tan.data.territory.TerritoryData;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.gui.cosmetic.type.IconBuilder;
import org.leralix.tan.gui.user.territory.relation.OpenDiplomacyProposalsMenu;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.utils.gameplay.TerritoryUtil;

public class DiplomacyProposal {

    private final String askingTerritoryID;
    private final String receivingTerritoryID;
    private final TownRelation relationProposal;

    public DiplomacyProposal(String askingTerritoryID, String receivingTerritoryID, TownRelation relationProposal) {
        this.askingTerritoryID = askingTerritoryID;
        this.receivingTerritoryID = receivingTerritoryID;
        this.relationProposal = relationProposal;
    }

    public IconBuilder createGuiItem(OpenDiplomacyProposalsMenu menu, LangType langType) {

        TerritoryData receivingTerritory = TerritoryUtil.getTerritory(receivingTerritoryID);
        TerritoryData askingTerritory = TerritoryUtil.getTerritory(askingTerritoryID);

        if (receivingTerritory == null) {
            return null;
        }

        if (askingTerritory == null) {
            receivingTerritory.removeDiplomaticProposal(askingTerritoryID);
            return null;
        }
        TownRelation currentRelation = askingTerritory.getRelationWith(receivingTerritory);

        return IconManager.getInstance().get(IconKey.DIPLOMACY_PROPOSAL_ICON)
                .setName(Lang.DIPLOMATIC_RELATION.get(langType, askingTerritory.getColoredName()))
                .setDescription(
                        Lang.DIPLOMATIC_RELATION_DESC1.get(relationProposal.getColoredName(langType)),
                        Lang.DIPLOMATIC_RELATION_DESC2.get(currentRelation.getColoredName(langType))
                )
                .setClickToAcceptMessage(
                        Lang.GUI_GENERIC_LEFT_CLICK_TO_ACCEPT
                )
                .setAction(event -> {
                    event.setCancelled(true);
                    if (event.isLeftClick()) {
                        askingTerritory.setRelation(receivingTerritory, relationProposal);
                        receivingTerritory.removeDiplomaticProposal(askingTerritoryID);
                    }
                    menu.open();
                });
    }
}
