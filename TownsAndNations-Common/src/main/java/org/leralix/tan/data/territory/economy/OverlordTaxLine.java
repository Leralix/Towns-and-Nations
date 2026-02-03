package org.leralix.tan.data.territory.economy;

import dev.triumphteam.gui.guis.Gui;
import org.bukkit.entity.Player;
import org.leralix.tan.data.territory.TerritoryData;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.utils.text.StringUtil;

public class OverlordTaxLine extends ProfitLine {

    double tax;

    public OverlordTaxLine(TerritoryData territoryData, TerritoryData overlord) {
        super(territoryData);
        tax = -overlord.getTax();
    }

    @Override
    protected double getMoney() {
        return tax;
    }

    public FilledLang getLine() {
        return Lang.OVERLORD_TAX_LINE.get(StringUtil.getColoredMoney(getMoney()));
    }

    @Override
    public void addItems(Gui gui, Player player, LangType langType) {

    }

    @Override
    public boolean isRecurrent() {
        return true;
    }


}
