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

public class DonationTransaction extends AbstractTransaction {


    private String playerID;
    private String territoryID;
    private double amount;

    public DonationTransaction(TerritoryData territoryData, Player player, double amount){
        this.playerID = territoryData.getID();
        this.territoryID = player.getUniqueId().toString();
        this.amount = amount;
    }

    public TransactionType getType(){
        return TransactionType.DONATION;
    }

    @Override
    public GuiItem getIcon(IconManager iconManager, Player player, LangType langType) {
        return iconManager.get(IconKey.BUDGET_ICON)
                .setName("Donation")
                .setDescription(Lang.DONATION_PAYMENT_HISTORY_LORE.get(playerID + " - " + territoryID, Double.toString(amount)))
                .asGuiItem(player, langType);
    }

    @Override
    protected void fromResultSet(ResultSet rs) throws SQLException {
        long timestamp = rs.getLong("timestamp");
        this.localDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
        this.territoryID = rs.getString("territory_id");
        this.playerID = rs.getString("player_id");
        this.amount = rs.getDouble("amount");
    }

    @Override
    public String getInsertSQL() {
        return "INSERT INTO transaction_payment (timestamp, territory_id, player_id, amount) VALUES (?, ?, ?, ?)";
    }

    @Override
    public void fillInsertStatement(PreparedStatement ps) throws SQLException {
        ps.setLong(1, getDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        ps.setString(2, territoryID);
        ps.setString(3, playerID);
        ps.setDouble(4, amount);
    }

    @Override
    public List<String> getConcerned() {
        List<String> res = new ArrayList<>();
        res.add(territoryID);
        res.add(playerID);
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
