package org.leralix.tan.storage.database.transactions.instance;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.database.transactions.AbstractTransaction;
import org.leralix.tan.storage.database.transactions.TransactionType;
import org.leralix.tan.utils.text.DateUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Set;

public class UpgradeTransaction extends AbstractTransaction {


    private final String territoryID;
    private final String upgradeID;
    private final int newLevel;
    private final double amount;

    public UpgradeTransaction(String territoryID, String upgradeID, int newLevel, double amount){
        this.territoryID = territoryID;
        this.upgradeID = upgradeID;
        this.newLevel = newLevel;
        this.amount = amount;
    }

    public UpgradeTransaction(ResultSet rs) throws SQLException {
        this.localDate = rs.getLong("timestamp");
        this.territoryID = rs.getString("territory_id");
        this.upgradeID = rs.getString("upgrade_id");
        this.newLevel = rs.getInt("upgrade_new_level");
        this.amount = rs.getDouble("amount");
    }

    public TransactionType getType(){
        return TransactionType.UPGRADE;
    }

    @Override
    public GuiItem getIcon(IconManager iconManager, Player player, LangType langType) {

        String upgradeName = getUpgradeName(territoryID, upgradeID, langType);

        return iconManager.get(IconKey.UPGRADE_REQUIREMENT)
                .setName(Lang.UPGRADE_TRANSACTION.get(langType))
                .setDescription(
                        Lang.TRANSACTION_FROM.get(getTerritoryName(territoryID, langType)),
                        Lang.TRANSACTION_UPGRADE.get(upgradeName, Integer.toString(newLevel)),
                        Lang.TRANSACTION_AMOUNT.get(Double.toString(amount)),
                        Lang.TRANSACTION_DATE.get(DateUtil.getRelativeTimeDescription(langType, getDate()))
                )
                .asGuiItem(player, langType);
    }

    @Override
    public String getInsertSQL() {
        return "INSERT INTO transaction_upgrades (timestamp, territory_id, upgrade_id, upgrade_new_level, amount) VALUES (?, ?, ?, ?, ?)";
    }

    @Override
    public void fillInsertStatement(PreparedStatement ps) throws SQLException {
        ps.setLong(1, getDate());
        ps.setString(2, territoryID);
        ps.setString(3, upgradeID);
        ps.setInt(4, newLevel);
        ps.setDouble(5, amount);
    }

    @Override
    public Set<String> getConcerned() {
        return Collections.singleton(territoryID);
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
