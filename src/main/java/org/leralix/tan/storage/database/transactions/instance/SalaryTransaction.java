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
import java.util.HashSet;
import java.util.Set;

public class SalaryTransaction extends AbstractTransaction {


    private final String territoryID;
    private final String playerID;
    private final double amount;

    public SalaryTransaction(String territoryID, String playerID, double amount){
        this.territoryID = territoryID;
        this.playerID = playerID;
        this.amount = amount;
    }

    public SalaryTransaction(ResultSet rs) throws SQLException {
        this.localDate = rs.getLong("timestamp");
        this.playerID = rs.getString("player_id");
        this.territoryID = rs.getString("territory_id");
        this.amount = rs.getDouble("amount");
    }

    public TransactionType getType(){
        return TransactionType.SALARY;
    }

    @Override
    public GuiItem getIcon(IconManager iconManager, Player player, LangType langType) {
        return iconManager.get(IconKey.SALARY_TRANSACTION)
                .setName(Lang.SALARY_TRANSACTION_NAME.get(langType))
                .setDescription(
                        Lang.TRANSACTION_FROM.get(getTerritoryName(territoryID, langType)),
                        Lang.TRANSACTION_TO.get(getPlayerName(playerID, langType)),
                        Lang.TRANSACTION_AMOUNT.get(Double.toString(amount)),
                        Lang.TRANSACTION_DATE.get(DateUtil.getRelativeTimeDescription(langType, getDate()))
                )
                .asGuiItem(player, langType);
    }

    @Override
    public String getInsertSQL() {
        return "INSERT INTO transaction_salary (timestamp, player_id, territory_id, amount) VALUES (?, ?, ?, ?)";
    }

    @Override
    public void fillInsertStatement(PreparedStatement ps) throws SQLException {
        ps.setLong(1, getDate());
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

    public double getAmount() {
        return amount;
    }
}
