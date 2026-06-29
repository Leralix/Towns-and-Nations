package org.leralix.tan.data.territory.economy;

import dev.triumphteam.gui.guis.Gui;
import org.bukkit.entity.Player;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.TerritoryData;
import org.leralix.tan.data.upgrade.rewards.numeric.ChunkCap;
import org.leralix.tan.data.upgrade.rewards.numeric.ChunkUpkeepCost;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.gui.user.territory.TreasuryMenu;
import org.leralix.tan.gui.user.territory.history.TerritoryTransactionHistory;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.database.transactions.TransactionType;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.constants.enums.ChunkCapExtendedStrategy;
import org.leralix.tan.utils.text.NumberUtil;
import org.leralix.tan.utils.text.StringUtil;

import java.util.Collections;
import java.util.List;

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
    public List<FilledLang> getLine() {
        int numberClaimedChunk = territoryData.getNumberOfClaimedChunk();
        int maxAmount = territoryData.getNewLevel().getStat(ChunkCap.class).getMaxAmount();
        if (Constants.getChunkCapExceededStrategy() == ChunkCapExtendedStrategy.INCREASE_UPKEEP && numberClaimedChunk > maxAmount) {
            double ratio = (double) numberClaimedChunk / maxAmount;
            double upkeep = getMoney() * ratio;
            return Collections.singletonList(Lang.TERRITORY_UPKEEP_LINE_OVEREXTENSION.get(StringUtil.getColoredMoney(upkeep), Double.toString(NumberUtil.roundWithDigits(ratio * 100))));
        } else {
            return Collections.singletonList(Lang.TERRITORY_UPKEEP_LINE.get(StringUtil.getColoredMoney(getMoney())));
        }
    }

    @Override
    public void addItems(Gui gui, Player player, ITanPlayer playerData, LangType lang) {
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
