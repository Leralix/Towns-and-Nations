package org.leralix.tan.gui.user.territory.hierarchy;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.lang.Lang;

import java.util.ArrayList;
import java.util.List;

public class AddVassalMenu extends IteratorGUI {

    private final TerritoryData territoryData;

    public AddVassalMenu(Player player, TerritoryData territoryData){
        super(player, Lang.HEADER_VASSALS, 6);
        this.territoryData = territoryData;
        open();
    }

    @Override
    public void open() {
        iterator(potentialVassals(), p -> new VassalsMenu(player, territoryData));
        gui.open(player);
    }

    private List<GuiItem> potentialVassals() {
        List<GuiItem> guiItems = new ArrayList<>();

        for (TerritoryData potentialVassal : territoryData.getPotentialVassals()) {
            if (territoryData.isVassal(potentialVassal) || potentialVassal.containsVassalisationProposal(territoryData))
                continue;

            guiItems.add(potentialVassal.getIconWithInformationAndRelation(territoryData, tanPlayer.getLang())
                    .setClickToAcceptMessage(Lang.GUI_REGION_INVITE_TOWN_DESC1)
                    .setAction(action -> {
                        potentialVassal.addVassalisationProposal(territoryData);
                        SoundUtil.playSound(player, SoundEnum.MINOR_GOOD);
                        open();
                    })
                    .asGuiItem(player, tanPlayer.getLang())
            );
        }
        return guiItems;
    }

}
