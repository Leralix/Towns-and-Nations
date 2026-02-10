package org.leralix.tan.data.territory.economy;

import dev.triumphteam.gui.guis.Gui;
import org.bukkit.entity.Player;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.TerritoryData;
import org.leralix.tan.data.territory.rank.RankData;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.gui.user.territory.TreasuryMenu;
import org.leralix.tan.gui.user.territory.history.TerritoryTransactionHistory;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.database.transactions.TransactionType;
import org.leralix.tan.utils.text.StringUtil;

public class SalaryPaymentLine extends ProfitLine {
    double totalSalaries;

    public SalaryPaymentLine(TerritoryData territoryData) {
        super(territoryData);
        totalSalaries = 0;

        for (RankData rankData : territoryData.getAllRanks()) {
            totalSalaries -= rankData.getSalary() * rankData.getPlayersID().size();
        }
    }

    @Override
    protected double getMoney() {
        return totalSalaries;
    }

    @Override
    public FilledLang getLine() {
        return Lang.PLAYER_SALARY_LINE.get(StringUtil.getColoredMoney(getMoney()));
    }

    @Override
    public void addItems(Gui gui, Player player, ITanPlayer playerData, LangType lang) {

        gui.setItem(2, 6, IconManager.getInstance().get(IconKey.SALARY_INFO_ICON)
                .setName(Lang.GUI_TREASURY_SALARY_HISTORY.get(lang))
                .setDescription(
                        Lang.GUI_TREASURY_SALARY_HISTORY_DESC1.get(StringUtil.getColoredMoney(getMoney())),
                        Lang.GUI_GENERIC_CLICK_TO_OPEN_HISTORY.get()
                )
                .setAction(action ->
                        new TerritoryTransactionHistory(player, territoryData, TransactionType.SALARY, p -> new TreasuryMenu(player, territoryData))
                )
                .asGuiItem(player, lang)
        );
    }

    @Override
    public boolean isRecurrent() {
        return true;
    }
}
