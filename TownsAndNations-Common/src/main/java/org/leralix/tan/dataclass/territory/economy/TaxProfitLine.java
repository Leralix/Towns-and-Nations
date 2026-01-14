package org.leralix.tan.dataclass.territory.economy;

import dev.triumphteam.gui.guis.Gui;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.utils.text.StringUtil;

public abstract class TaxProfitLine extends ProfitLine {

    protected double actualTaxes = 0;
    protected double missingTaxes = 0;

    protected TaxProfitLine(TerritoryData territoryData) {
        super(territoryData);
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
    public final void addItems(Gui gui, Player player, LangType lang) {
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
