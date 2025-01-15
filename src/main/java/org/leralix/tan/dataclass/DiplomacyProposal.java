package org.leralix.tan.dataclass;

import org.leralix.lib.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.tan.utils.HeadUtils;
import org.leralix.tan.utils.TerritoryUtil;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.enums.TownRelation;
import org.leralix.tan.gui.PlayerGUI;
import org.leralix.tan.lang.Lang;

import java.util.function.Consumer;

public class DiplomacyProposal {

    private final String askingTerritoryID;
    private final String receivingTerritoryID;
    private final TownRelation relationProposal;

    public DiplomacyProposal(String askingTerritoryID,String receivingTerritoryID, TownRelation relationProposal) {
        this.askingTerritoryID = askingTerritoryID;
        this.receivingTerritoryID = receivingTerritoryID;
        this.relationProposal = relationProposal;
    }

    public GuiItem createGuiItem(Player player, TerritoryData territoryData, int page, Consumer<Player> exitMenu) {

        TerritoryData receivingTerritory = TerritoryUtil.getTerritory(receivingTerritoryID);
        TerritoryData askingTerritory = TerritoryUtil.getTerritory(askingTerritoryID);

        if(askingTerritory == null){
            receivingTerritory.removeDiplomaticProposal(askingTerritoryID);
            return null;
        }

        TownRelation currentRelation = askingTerritory.getRelationWith(receivingTerritory);

        ItemStack diplomaticItem = HeadUtils.makeSkullURL(Lang.DIPLOMATIC_RELATION.get(askingTerritory.getColoredName()),"https://textures.minecraft.net/texture/1818d1cc53c275c294f5dfb559174dd931fc516a85af61a1de256aed8bca5e7",
                Lang.DIPLOMATIC_RELATION_DESC1.get(relationProposal.getColoredName()),
                Lang.DIPLOMATIC_RELATION_DESC2.get(currentRelation.getColoredName()),
                Lang.LEFT_CLICK_TO_ACCEPT.get());

        return new GuiItem(diplomaticItem, event -> {
            event.setCancelled(true);
            if (event.isLeftClick()) {
                askingTerritory.setRelation(receivingTerritory, relationProposal);
                receivingTerritory.removeDiplomaticProposal(askingTerritoryID);
            }
            PlayerGUI.openProposalMenu(player, territoryData, page, exitMenu);
        });



    }

}
