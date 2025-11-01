package org.leralix.tan.storage.database.transactions.instance;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
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
import java.util.ArrayList;
import java.util.List;

public class SalaryTransaction extends AbstractTransaction {


    private String territoryID;
    private String playerID;
    private double amount;

    public SalaryTransaction(String territoryID, String playerID, double amount){
        this.territoryID = territoryID;
        this.playerID = playerID;
        this.amount = amount;
    }

    public TransactionType getType(){
        return TransactionType.SALARY;
    }

    @Override
    public GuiItem getIcon(IconManager iconManager, Player player, LangType langType) {
        return iconManager.get(IconKey.PLAYER_HEAD_ICON)
                .setName("Donation")
                .setDescription(Lang.DONATION_PAYMENT_HISTORY_LORE.get(territoryID + " - " + playerID, Double.toString(amount)))
                .asGuiItem(player, langType);
    }

    @Override
    protected void fromResultSet(ResultSet rs) throws SQLException {
        long timestamp = rs.getLong("timestamp");
        this.localDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
        this.playerID = rs.getString("player_id");
        this.territoryID = rs.getString("territory_id");
        this.amount = rs.getDouble("amount");
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
    public List<String> getConcerned() {
        List<String> res = new ArrayList<>();
        res.add(playerID);
        res.add(territoryID);
        return res;
    }

    public double getAmount() {
        return amount;
    }
}
