package org.leralix.tan.dataclass.territory.economy;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.tan.dataclass.territory.ITerritoryData;
import org.leralix.tan.gui.PlayerGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.utils.HeadUtils;
import org.leralix.tan.utils.SoundUtil;

import static org.leralix.tan.enums.SoundEnum.ADD;
import static org.leralix.tan.enums.SoundEnum.REMOVE;
import static org.leralix.tan.utils.ChatUtils.getTANString;

public class OverlordTaxLine extends ProfitLine {

    double tax;

    public OverlordTaxLine(ITerritoryData territoryData){
        super(territoryData);
        ITerritoryData overlordData = territoryData.getOverlord();
        if(overlordData == null)
            return;
        tax = -overlordData.getTax();
    }

    public String getLine() {
        return Lang.OVERLORD_TAX_LINE.get(getColoredMoney(tax));
    }

    @Override
    public void addItems(Gui gui, Player player) {

    }


}
