package org.leralix.tan.data.territory.economy;

import dev.triumphteam.gui.guis.Gui;
import org.bukkit.entity.Player;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.Territory;
import org.leralix.tan.data.territory.wargoals.Tribute;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.utils.gameplay.TerritoryUtil;

import java.util.ArrayList;
import java.util.List;

public class TributeReceivedTaxLine extends ProfitLine {

    public TributeReceivedTaxLine(Territory territory) {
        super(territory);
    }

    @Override
    public double getMoney() {
        double amount = 0;
        for (Tribute tribute : TownsAndNations.getPlugin().getTributeStorage().getTributeOfMaster(territoryData)) {
            amount += tribute.getRemaningDailyAmount();
        }
        return amount;
    }

    @Override
    public List<FilledLang> getLine() {
        List<FilledLang> res = new ArrayList<>();
        for (Tribute tribute : TownsAndNations.getPlugin().getTributeStorage().getTributeOfMaster(territoryData)) {
            Territory territory = TerritoryUtil.getTerritory(tribute.getTributaryID());
            if (territory == null) {
                continue;
            }
            res.add(Lang.TRIBUTE_RECEIVED_TAX_LINE.get(
                    territory.getColoredName(),
                    Integer.toString(tribute.getRemaningDailyAmount()),
                    Integer.toString(tribute.getAmountPaid()),
                    Integer.toString(tribute.getTotalAmount())
            ));
        }
        return res;
    }

    @Override
    public void addItems(Gui gui, Player player, ITanPlayer tanPlayer, LangType lang) {

    }

    @Override
    public boolean isRecurrent() {
        return true;
    }
}
