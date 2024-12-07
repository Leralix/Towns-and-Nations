package org.leralix.tan.dataclass.territory.economy;

import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.tan.dataclass.newhistory.TransactionHistoryEnum;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.gui.PlayerGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.utils.HeadUtils;
import org.leralix.tan.utils.StringUtil;

public class ChunkUpkeepLine extends ProfitLine {
    private final double totalUpkeep;

    public ChunkUpkeepLine(TerritoryData territoryData) {
        super(territoryData);
        this.totalUpkeep = territoryData.getNumberOfClaimedChunk() * -territoryData.getChunkUpkeepCost();
    }

    @Override
    protected double getMoney() {
        return totalUpkeep;
    }

    @Override
    public String getLine() {
        return Lang.TERRITORY_UPKEEP_LINE.get(StringUtil.getColoredMoney(getMoney()));
    }

    @Override
    public void addItems(Gui gui, Player player) {
        ItemStack chunkSpending = HeadUtils.makeSkullB64(Lang.GUI_TREASURY_CHUNK_SPENDING_HISTORY.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTc5ODBiOTQwYWY4NThmOTEwOTQzNDY0ZWUwMDM1OTI4N2NiMGI1ODEwNjgwYjYwYjg5YmU0MjEwZGRhMGVkMSJ9fX0=",
                Lang.GUI_TREASURY_CHUNK_SPENDING_HISTORY_DESC1.get(StringUtil.getColoredMoney(getMoney())),
                Lang.GUI_TREASURY_CHUNK_SPENDING_HISTORY_DESC2.get(StringUtil.getColoredMoney(-territoryData.getChunkUpkeepCost())),
                Lang.GUI_TREASURY_CHUNK_SPENDING_HISTORY_DESC3.get(territoryData.getNumberOfClaimedChunk()));
        GuiItem chunkSpendingItem = new GuiItem(chunkSpending, event -> {
            PlayerGUI.openTownEconomicsHistory(player, territoryData, TransactionHistoryEnum.CHUNK_SPENDING);
            event.setCancelled(true);
        });
        gui.setItem(2,7, chunkSpendingItem);
    }

    @Override
    public boolean isRecurrent() {
        return true;
    }
}
