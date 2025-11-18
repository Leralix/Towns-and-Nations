package org.leralix.tan.storage.database.transactions.instance;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.database.transactions.AbstractTransaction;
import org.leralix.tan.storage.database.transactions.TransactionType;
import org.leralix.tan.utils.text.NumberUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.Set;

public class TerritoryChunkUpkeepTransaction extends AbstractTransaction {

    private final String territoryID;
    private final double costPerChunk;
    private final int numberOfChunks;
    private final boolean enoughMoney;

    public TerritoryChunkUpkeepTransaction(String territoryID, double costPerChunk, int numberOfChunks, boolean enoughMoney) {
        this.territoryID = territoryID;
        this.costPerChunk = costPerChunk;
        this.numberOfChunks = numberOfChunks;
        this.enoughMoney = enoughMoney;
    }

    public TerritoryChunkUpkeepTransaction(ResultSet rs) throws SQLException {
        this.territoryID = rs.getString("territory_id");
        this.costPerChunk = rs.getDouble("cost_per_chunk");
        this.numberOfChunks = rs.getInt("number_of_chunks");
        this.enoughMoney = rs.getBoolean("enough_money");
    }


    @Override
    public TransactionType getType() {
        return TransactionType.TERRITORY_CHUNK_UPKEEP;
    }

    public double getCost(){
        return NumberUtil.roundWithDigits(costPerChunk * numberOfChunks);
    }

    @Override
    public GuiItem getIcon(IconManager iconManager, Player player, LangType langType) {
        return iconManager.get(IconKey.CHUNK_UPKEEP_TRANSACTION)
                .setName(Lang.TERRITORY_CHUNK_UPKEEP_TRANSACTION_SCOPE.get(langType))
                .setDescription(
                        Lang.TRANSACTION_FROM.get(getTerritoryName(territoryID, langType)),
                        enoughMoney ? Lang.TRANSACTION_AMOUNT.get(Double.toString(getCost())) : Lang.TRANSACTION_FAILED_NOT_ENOUGH_MONEY.get(Double.toString(getCost())),
                        Lang.CHUNK_UPKEEP_INFO.get(Integer.toString(numberOfChunks), Double.toString(costPerChunk))
                )
                .asGuiItem(player, langType);
    }

    @Override
    public String getInsertSQL() {
        return "INSERT INTO transaction_territory_chunk_upkeep (timestamp, territory_id, cost_per_chunk, number_of_chunks, enough_money) VALUES (?, ?, ?, ?, ?)";
    }

    @Override
    public void fillInsertStatement(PreparedStatement ps) throws SQLException {
        ps.setLong(1, getDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        ps.setString(2, territoryID);
        ps.setDouble(3, costPerChunk);
        ps.setInt(4, numberOfChunks);
        ps.setBoolean(5, enoughMoney);
    }

    @Override
    public Set<String> getConcerned() {
        return Set.of(territoryID);
    }
}
