package org.leralix.tan.dataclass.territory.economy;

import dev.triumphteam.gui.guis.Gui;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.utils.text.StringUtil;

public class SubjectTaxLine extends ProfitLine {

    double actualTaxes = 0;
    double missingTaxes = 0;

    public SubjectTaxLine(RegionData regionData) {
        super(regionData);
        double tax = regionData.getTax();
        for (TerritoryData townData : regionData.getVassals()) {
            if (townData.getBalance() > tax)
                actualTaxes += tax;
            else
                missingTaxes += tax;
        }
    }

    @Override
    protected double getMoney() {
        return actualTaxes;
    }

    @Override
    public FilledLang getLine() {
        if (missingTaxes > 0)
            return Lang.PLAYER_TAX_MISSING_LINE.get(StringUtil.getColoredMoney(getMoney()), Double.toString(missingTaxes));
        else
            return Lang.PLAYER_TAX_LINE.get(StringUtil.getColoredMoney(getMoney()));
    }

    @Override
    public void addItems(Gui gui, Player player, LangType lang) {
        double taxRate = territoryData.getTax();
        addDecreaseItem(gui, player, lang, taxRate);
        addInfoItem(gui, player, lang, taxRate);
        addIncreaseItem(gui, player, lang);
    }

    @Override
    public boolean isRecurrent() {
        return true;
    }
}
