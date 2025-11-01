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
import java.util.Collections;
import java.util.List;

public class UpgradeTransaction extends AbstractTransaction {


    private String territoryID;
    private String upgradeID;
    private int newLevel;
    private double amount;

    public UpgradeTransaction(String territoryID, String upgradeID, int newLevel, double amount){
        this.territoryID = territoryID;
        this.upgradeID = upgradeID;
        this.newLevel = newLevel;
        this.amount = amount;
    }

    public TransactionType getType(){
        return TransactionType.DONATION;
    }

    @Override
    public GuiItem getIcon(IconManager iconManager, Player player, LangType langType) {
        return iconManager.get(IconKey.PLAYER_HEAD_ICON)
                .setName("Donation")
                .setDescription(Lang.DONATION_PAYMENT_HISTORY_LORE.get(territoryID + " - " + upgradeID + " - " + newLevel , Double.toString(amount)))
                .asGuiItem(player, langType);
    }

    @Override
    protected void fromResultSet(ResultSet rs) throws SQLException {
        long timestamp = rs.getLong("timestamp");
        this.localDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
        this.territoryID = rs.getString("territory_id");
        this.upgradeID = rs.getString("upgrade_id");
        this.newLevel = rs.getInt("upgrade_new_level");
        this.amount = rs.getDouble("amount");
    }

    @Override
    public String getInsertSQL() {
        return "INSERT INTO transaction_taxes (timestamp, territory_id, upgrade_id, upgrade_new_level, amount) VALUES (?, ?, ?, ?, ?)";
    }

    @Override
    public void fillInsertStatement(PreparedStatement ps) throws SQLException {
        ps.setLong(1, getDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        ps.setString(2, territoryID);
        ps.setString(3, upgradeID);
        ps.setInt(4, newLevel);
        ps.setDouble(5, amount);
    }

    @Override
    public List<String> getConcerned() {
        return Collections.singletonList(territoryID);
    }

    public String getTerritoryID() {
        return territoryID;
    }

    public String getUpgradeID() {
        return upgradeID;
    }

    public int getNewLevel() {
        return newLevel;
    }

    public double getAmount() {
        return amount;
    }
}
