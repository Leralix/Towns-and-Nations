package org.leralix.tan.storage.database.transactions.instance;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.lang.FilledLang;
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

public class TerritoryTaxTransaction extends AbstractTransaction {


    private final String senderID;
    private final String recieverID;
    private final double amount;
    private final boolean enoughMoney;

    public TerritoryTaxTransaction(String senderID, String recieverID, double amount, boolean enoughMoney){
        this.senderID = senderID;
        this.recieverID = recieverID;
        this.amount = amount;
        this.enoughMoney = enoughMoney;
    }

    public TerritoryTaxTransaction(ResultSet rs) throws SQLException {
        this.localDate = rs.getLong("timestamp");
        this.senderID = rs.getString("sender_id");
        this.recieverID = rs.getString("receiver_id");
        this.amount = rs.getDouble("amount");
        this.enoughMoney = rs.getBoolean("enough_money");
    }

    public TransactionType getType(){
        return TransactionType.TERRITORY_TAX;
    }

    @Override
    public GuiItem getIcon(IconManager iconManager, Player player, LangType langType) {
        return iconManager.get(IconKey.TERRITORY_TAX_ICON)
                .setName(Lang.TERRITORY_TAX_TRANSACTION.get(langType))
                .setDescription(
                        Lang.TRANSACTION_FROM.get(getTerritoryName(senderID, langType)),
                        Lang.TRANSACTION_TO.get(getTerritoryName(recieverID, langType)),
                        Lang.TRANSACTION_AMOUNT.get(Double.toString(amount)),
                        Lang.TRANSACTION_DATE.get(DateUtil.getRelativeTimeDescription(langType, getDate()))
                )
                .asGuiItem(player, langType);
    }

    @Override
    public String getInsertSQL() {
        return "INSERT INTO transaction_territory_taxes (timestamp, sender_id, receiver_id, amount, enough_money) VALUES (?, ?, ?, ?, ?)";
    }

    @Override
    public void fillInsertStatement(PreparedStatement ps) throws SQLException {
        ps.setLong(1, getDate());
        ps.setString(2, senderID);
        ps.setString(3, recieverID);
        ps.setDouble(4, amount);
        ps.setBoolean(5, enoughMoney);
    }

    @Override
    public Set<String> getConcerned() {
        Set<String> res = new HashSet<>();
        res.add(senderID);
        res.add(recieverID);
        return res;
    }

    public String getPlayerID() {
        return senderID;
    }

    public String getTownID() {
        return recieverID;
    }

    public double getAmount() {
        return amount;
    }

    public FilledLang getDailyLine(LangType langType) {
        if(enoughMoney){
            return Lang.TRANSACTION_DAILY.get(getTerritoryName(senderID, langType), Double.toString(amount));
        }
        else{
            return Lang.TRANSACTION_DAILY_NOT_ENOUGH_MONEY.get(getTerritoryName(senderID, langType));
        }
    }
}
