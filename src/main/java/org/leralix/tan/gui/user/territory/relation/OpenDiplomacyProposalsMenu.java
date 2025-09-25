package org.leralix.tan.gui.user.territory.relation;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.DiplomacyProposal;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;

import java.util.ArrayList;
import java.util.List;

public class OpenDiplomacyProposalsMenu extends IteratorGUI {

    private final TerritoryData territoryData;

    public OpenDiplomacyProposalsMenu(Player player, TerritoryData territoryData) {
        super(player, Lang.HEADER_RELATIONS.get(player, territoryData.getName()), 6);
        this.territoryData = territoryData;
        open();
    }


    @Override
    public void open() {
        iterator(getDiplomacyProposals(langType), p -> new OpenDiplomacyMenu(player, territoryData));
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
