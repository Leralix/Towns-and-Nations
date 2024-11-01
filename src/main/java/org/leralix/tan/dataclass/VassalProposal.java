package org.leralix.tan.dataclass;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.inventory.ItemStack;
import org.leralix.tan.dataclass.territory.ITerritoryData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.utils.HeadUtils;
import org.leralix.tan.utils.TerritoryUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VassalProposal {

    List<String> overlordsProposals;

    public VassalProposal(){
        overlordsProposals = new ArrayList<>();
    }

    public void addProposal(ITerritoryData proposal){
        overlordsProposals.add(proposal.getID());
    }

    public void removeProposal(ITerritoryData proposal){
        overlordsProposals.remove(proposal.getID());
    }

    public int getNumberOfProposals(){
        return overlordsProposals.size();
    }

    public List<GuiItem> getAllProposals(){
        ArrayList<GuiItem> proposals = new ArrayList<>();
        for(String proposal : overlordsProposals) {
            ITerritoryData territory = TerritoryUtil.getTerritory(proposal);
            if (territory == null)
                continue;
            ItemStack territoryItem = territory.getIconWithInformations();
            HeadUtils.addLore(territoryItem, Lang.LEFT_CLICK_TO_ACCEPT.get(), Lang.RIGHT_CLICK_TO_REFUSE.get());
            GuiItem acceptInvitation = ItemBuilder.from(territoryItem).asGuiItem(event -> {
                event.setCancelled(true);

            });
            proposals.add(acceptInvitation);
        }
        return proposals;
    }

}
