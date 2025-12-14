package org.leralix.tan.dataclass.territory.economy;

import dev.triumphteam.gui.guis.Gui;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.LangType;

public abstract class ProfitLine implements Comparable<ProfitLine> {

    protected final TerritoryData territoryData;

    protected ProfitLine(TerritoryData territoryData) {
        this.territoryData = territoryData;
    }

    protected abstract double getMoney();

    public abstract FilledLang getLine();

    public abstract void addItems(Gui gui, Player player, LangType langType);

    @Override
    public int compareTo(ProfitLine otherLine) {
        return Double.compare(otherLine.getMoney(), getMoney());
    }

    public abstract boolean isRecurrent();
}
