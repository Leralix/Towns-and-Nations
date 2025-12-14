package org.leralix.tan.storage.database.transactions.instance;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.territory.TerritoryData;
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

public class PlayerTaxTransaction extends AbstractTransaction {


    private final String playerID;
    private final String townID;
    private final double amount;
    private final boolean enoughMoney;

    public PlayerTaxTransaction(TerritoryData territoryData, String playerID, double amount, boolean enoughMoney){
        this.playerID = playerID;
        this.townID = territoryData.getID();
        this.amount = amount;
        this.enoughMoney = enoughMoney;
    }

    public PlayerTaxTransaction(ResultSet rs) throws SQLException {
        this.localDate = rs.getLong("timestamp");
        this.townID = rs.getString("territory_id");
        this.playerID = rs.getString("player_id");
        this.amount = rs.getDouble("amount");
        this.enoughMoney = rs.getBoolean("enough_money");
    }

    public TransactionType getType(){
        return TransactionType.TAXES;
    }

    @Override
    public GuiItem getIcon(IconManager iconManager, Player player, LangType langType) {
        return iconManager.get(IconKey.PLAYER_TAX_TRANSACTION)
                .setName(Lang.PLAYER_TAX_TRANSACTION.get(langType))
                .setDescription(
                        Lang.TRANSACTION_FROM.get(getPlayerName(playerID, langType)),
                        Lang.TRANSACTION_TO.get(getTerritoryName(townID, langType)),
                        Lang.TRANSACTION_AMOUNT.get(Double.toString(amount)),
                        Lang.TRANSACTION_DATE.get(DateUtil.getRelativeTimeDescription(langType, getDate()))
                )
                .asGuiItem(player, langType);
    }

    @Override
    public String getInsertSQL() {
        return "INSERT INTO transaction_taxes (timestamp, player_id, territory_id, amount, enough_money) VALUES (?, ?, ?, ?, ?)";
    }

    @Override
    public void fillInsertStatement(PreparedStatement ps) throws SQLException {
        ps.setLong(1, getDate());
        ps.setString(2, playerID);
        ps.setString(3, townID);
        ps.setDouble(4, amount);
        ps.setBoolean(5, enoughMoney);
    }

    @Override
    public Set<String> getConcerned() {
        Set<String> res = new HashSet<>();
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

    public FilledLang getDailyLine(LangType langType) {

        if(enoughMoney){
            return Lang.TRANSACTION_DAILY.get(getPlayerName(playerID, langType), Double.toString(amount));
        }
        else{
            return Lang.TRANSACTION_DAILY_NOT_ENOUGH_MONEY.get(getPlayerName(playerID, langType));
        }
    }
}
