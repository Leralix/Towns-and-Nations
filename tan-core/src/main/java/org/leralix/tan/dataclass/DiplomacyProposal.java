package org.leralix.tan.dataclass;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.inventory.ItemStack;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.enums.TownRelation;
import org.leralix.tan.gui.user.territory.relation.OpenDiplomacyProposalsMenu;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.utils.deprecated.HeadUtils;
import org.leralix.tan.utils.gameplay.TerritoryUtil;

public class DiplomacyProposal {

  private final String askingTerritoryID;
  private final String receivingTerritoryID;
  private final TownRelation relationProposal;

  public DiplomacyProposal(
      String askingTerritoryID, String receivingTerritoryID, TownRelation relationProposal) {
    this.askingTerritoryID = askingTerritoryID;
    this.receivingTerritoryID = receivingTerritoryID;
    this.relationProposal = relationProposal;
  }

  public GuiItem createGuiItem(OpenDiplomacyProposalsMenu menu, LangType langType) {

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

    ItemStack diplomaticItem =
        HeadUtils.makeSkullURL(
            Lang.DIPLOMATIC_RELATION.get(langType, askingTerritory.getBaseColoredName()),
            "https://textures.minecraft.net/texture/1818d1cc53c275c294f5dfb559174dd931fc516a85af61a1de256aed8bca5e7",
            Lang.DIPLOMATIC_RELATION_DESC1.get(langType, relationProposal.getColoredName(langType)),
            Lang.DIPLOMATIC_RELATION_DESC2.get(langType, currentRelation.getColoredName(langType)),
            Lang.GUI_GENERIC_LEFT_CLICK_TO_ACCEPT.get(langType));

    return new GuiItem(
        diplomaticItem,
        event -> {
          event.setCancelled(true);
          if (event.isLeftClick()) {
            askingTerritory.setRelation(receivingTerritory, relationProposal);
            receivingTerritory.removeDiplomaticProposal(askingTerritoryID);
          }
          menu.open();
        });
  }
}
