package org.leralix.tan.gui.user.territory.relation;

import dev.triumphteam.gui.guis.GuiItem;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.DiplomacyProposal;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.stored.PlayerDataStorage;

public class OpenDiplomacyProposalsMenu extends IteratorGUI {

  private final TerritoryData territoryData;

  private OpenDiplomacyProposalsMenu(
      Player player, ITanPlayer tanPlayer, TerritoryData territoryData) {
    super(
        player,
        tanPlayer,
        Lang.HEADER_RELATIONS.get(tanPlayer.getLang(), territoryData.getName()),
        6);
    this.territoryData = territoryData;
  }

  public static void open(Player player, TerritoryData territoryData) {
    PlayerDataStorage.getInstance()
        .get(player)
        .thenAccept(
            tanPlayer -> {
              new OpenDiplomacyProposalsMenu(player, tanPlayer, territoryData).open();
            });
  }

  @Override
  public void open() {
    iterator(getDiplomacyProposals(langType), p -> OpenDiplomacyMenu.open(p, territoryData));
    gui.open(player);
  }

  private List<GuiItem> getDiplomacyProposals(LangType langType) {
    ArrayList<GuiItem> guiItems = new ArrayList<>();

    for (DiplomacyProposal diplomacyProposal : territoryData.getAllDiplomacyProposal()) {
      guiItems.add(diplomacyProposal.createGuiItem(this, langType));
    }
    return guiItems;
  }
}
