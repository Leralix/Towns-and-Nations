package org.leralix.tan.storage.database.transactions.instance;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.database.transactions.AbstractTransaction;
import org.leralix.tan.storage.database.transactions.TransactionType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Set;

public class RetrieveTransaction extends AbstractTransaction {


    private final String playerID;
    private final String territoryID;
    private final double amount;

    public RetrieveTransaction(TerritoryData territoryData, Player player, double amount){
        this.playerID = player.getUniqueId().toString();
        this.territoryID = territoryData.getID();
        this.amount = amount;
    }

    public RetrieveTransaction(ResultSet rs) throws SQLException {
        long timestamp = rs.getLong("timestamp");
        this.localDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
        this.territoryID = rs.getString("territory_id");
        this.playerID = rs.getString("player_id");
        this.amount = rs.getDouble("amount");
    }

    public TransactionType getType(){
        return TransactionType.RETRIEVE;
    }

    @Override
    public GuiItem getIcon(IconManager iconManager, Player player, LangType langType) {
        return iconManager.get(IconKey.WITHDRAWAL_TRANSACTION)
                .setName(Lang.RETRIEVE_TRANSACTION_NAME.get(langType))
                .setDescription(
                        Lang.TRANSACTION_FROM.get(getTerritoryName(territoryID, langType)),
                        Lang.TRANSACTION_TO.get(getPlayerName(playerID, langType)),
                        Lang.TRANSACTION_AMOUNT.get(Double.toString(amount))
                )
                .asGuiItem(player, langType);
    }

    @Override
    public String getInsertSQL() {
        return "INSERT INTO transaction_retrieve (timestamp, player_id, territory_id, amount) VALUES (?, ?, ?, ?)";
    }

    @Override
    public void fillInsertStatement(PreparedStatement ps) throws SQLException {
        ps.setLong(1, getDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        ps.setString(2, playerID);
        ps.setString(3, territoryID);
        ps.setDouble(4, amount);
    }

    @Override
    public Set<String> getConcerned() {
        Set<String> res = new HashSet<>();
        res.add(playerID);
        res.add(territoryID);
        return res;
    }

    public String getPlayerID() {
        return playerID;
    }

    public String getTerritoryID() {
        return territoryID;
    }

    public double getAmount() {
        return amount;
    }
}
