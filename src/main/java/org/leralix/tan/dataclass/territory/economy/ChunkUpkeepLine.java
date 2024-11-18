package org.leralix.tan.dataclass.territory.economy;

import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.leralix.tan.dataclass.territory.ITerritoryData;
import org.leralix.tan.enums.HistoryEnum;
import org.leralix.tan.gui.PlayerGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.utils.HeadUtils;

public class ChunkUpkeepLine extends ProfitLine {
    private final ITerritoryData territoryData;
    private final double totalUpkeep;

    public ChunkUpkeepLine(ITerritoryData territoryData) {
        super(territoryData);
        this.territoryData = territoryData;
        this.totalUpkeep = territoryData.getNumberOfClaimedChunk() * -territoryData.getChunkUpkeepCost();
    }

    @Override
    public String getLine() {
        return Lang.TERRITORY_UPKEEP_LINE.get(getColoredMoney(totalUpkeep));
    }

    @Override
    public void addItems(Gui gui, Player player) {
        ItemStack chunkSpending = HeadUtils.makeSkullB64(Lang.GUI_TREASURY_CHUNK_SPENDING_HISTORY.get(),"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTc5ODBiOTQwYWY4NThmOTEwOTQzNDY0ZWUwMDM1OTI4N2NiMGI1ODEwNjgwYjYwYjg5YmU0MjEwZGRhMGVkMSJ9fX0=",
                Lang.GUI_TREASURY_CHUNK_SPENDING_HISTORY_DESC1.get(getColoredMoney(totalUpkeep)),
                Lang.GUI_TREASURY_CHUNK_SPENDING_HISTORY_DESC2.get(0),
                Lang.GUI_TREASURY_CHUNK_SPENDING_HISTORY_DESC3.get(0));
        GuiItem chunkSpendingItem = new GuiItem(chunkSpending, event -> {
            PlayerGUI.openTownEconomicsHistory(player, territoryData, HistoryEnum.CHUNK);
            event.setCancelled(true);
        });
        gui.setItem(2,7, chunkSpendingItem);
    }
}