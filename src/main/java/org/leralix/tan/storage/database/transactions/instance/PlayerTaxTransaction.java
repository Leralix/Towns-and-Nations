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
import java.util.ArrayList;
import java.util.List;

public class PlayerTaxTransaction extends AbstractTransaction {


    private String playerID;
    private String townID;
    private double amount;
    private boolean enoughMoney;

    public PlayerTaxTransaction(TerritoryData territoryData, String playerID, double amount, boolean enoughMoney){
        this.playerID = territoryData.getID();
        this.townID = playerID;
        this.amount = amount;
        this.enoughMoney = enoughMoney;
    }

    public TransactionType getType(){
        return TransactionType.TAXES;
    }

    @Override
    public GuiItem getIcon(IconManager iconManager, Player player, LangType langType) {
        return iconManager.get(IconKey.BUDGET_ICON)
                .setName("Donation")
                .setDescription(Lang.DONATION_PAYMENT_HISTORY_LORE.get(playerID + " - " + townID, Double.toString(amount)))
                .asGuiItem(player, langType);
    }

    @Override
    protected void fromResultSet(ResultSet rs) throws SQLException {
        long timestamp = rs.getLong("timestamp");
        this.localDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
        this.townID = rs.getString("territory_id");
        this.playerID = rs.getString("player_id");
        this.amount = rs.getDouble("amount");
        this.enoughMoney = rs.getBoolean("enough_money");
    }

    @Override
    public String getInsertSQL() {
        return "INSERT INTO transaction_taxes (timestamp, player_id, territory_id, amount, enough_money) VALUES (?, ?, ?, ?, ?)";
    }

    @Override
    public void fillInsertStatement(PreparedStatement ps) throws SQLException {
        ps.setLong(1, getDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        ps.setString(2, playerID);
        ps.setString(3, townID);
        ps.setDouble(4, amount);
        ps.setBoolean(5, enoughMoney);
    }

    @Override
    public List<String> getConcerned() {
        List<String> res = new ArrayList<>();
        res.add(playerID);
        res.add(townID);
        return res;
    }

    public String getPlayerID() {
        return playerID;
    }

    public String getTownID() {
        return townID;
    }

    public double getAmount() {
        return amount;
    }
}
