package org.leralix.tan.data.territory.economy;

import dev.triumphteam.gui.guis.Gui;
import org.bukkit.entity.Player;
import org.leralix.tan.data.territory.TerritoryData;
import org.leralix.tan.data.upgrade.rewards.numeric.ChunkUpkeepCost;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.gui.user.territory.TreasuryMenu;
import org.leralix.tan.gui.user.territory.history.TerritoryTransactionHistory;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.database.transactions.TransactionType;
import org.leralix.tan.utils.text.StringUtil;

public class ChunkUpkeepLine extends ProfitLine {
    private final double totalUpkeep;

    public ChunkUpkeepLine(TerritoryData territoryData) {
        super(territoryData);
        this.totalUpkeep = -(territoryData.getNumberOfClaimedChunk() * territoryData.getNewLevel().getStat(ChunkUpkeepCost.class).getCost());
    }

    @Override
    protected double getMoney() {
        return totalUpkeep;
    }

    @Override
    public FilledLang getLine() {
        return Lang.TERRITORY_UPKEEP_LINE.get(StringUtil.getColoredMoney(getMoney()));
    }

    @Override
    public void addItems(Gui gui, Player player, LangType lang) {
        gui.setItem(2, 7, IconManager.getInstance().get(IconKey.CHUNK_UPKEEP_ICON)
                .setName(Lang.GUI_TREASURY_CHUNK_SPENDING_HISTORY.get(lang))
                .setDescription(
                        Lang.GUI_TREASURY_CHUNK_SPENDING_HISTORY_DESC1.get(StringUtil.getColoredMoney(getMoney())),
                        Lang.GUI_TREASURY_CHUNK_SPENDING_HISTORY_DESC2.get(StringUtil.getColoredMoney(-territoryData.getNewLevel().getStat(ChunkUpkeepCost.class).getCost())),
                        Lang.GUI_TREASURY_CHUNK_SPENDING_HISTORY_DESC3.get(Integer.toString(territoryData.getNumberOfClaimedChunk()))
                )
                .setAction(event ->
                        new TerritoryTransactionHistory(player, territoryData, TransactionType.TERRITORY_CHUNK_UPKEEP, p -> new TreasuryMenu(player, territoryData))
                )
                .asGuiItem(player, lang)
        );
    }

    @Override
    public boolean isRecurrent() {
        return true;
    }
}
