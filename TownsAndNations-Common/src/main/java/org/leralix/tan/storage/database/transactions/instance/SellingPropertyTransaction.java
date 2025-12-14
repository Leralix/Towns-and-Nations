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

public class SellingPropertyTransaction extends AbstractTransaction {


    private final String territoryID;
    private final String propertyID;
    private final String sellerID;
    private final String buyerID;
    private final double amount;
    private final double taxPercentage;

    public SellingPropertyTransaction(
            String territoryID,
            String propertyID,
            String sellerID,
            String buyerID,
            double amount,
            double taxPercentage
    ) {
        this.territoryID = territoryID;
        this.propertyID = propertyID;
        this.sellerID = sellerID;
        this.buyerID = buyerID;
        this.amount = amount;
        this.taxPercentage = taxPercentage;
    }

    public SellingPropertyTransaction(ResultSet rs) throws SQLException {
        this.localDate = rs.getLong("timestamp");
        this.territoryID = rs.getString("territory_id");
        this.propertyID = rs.getString("property_id");
        this.sellerID = rs.getString("seller_id");
        this.buyerID = rs.getString("buyer_id");
        this.amount = rs.getDouble("amount");
        this.taxPercentage = rs.getInt("tax_percentage");
    }

    public TransactionType getType() {
        return TransactionType.SELLING_PROPERTY;
    }

    @Override
    public GuiItem getIcon(IconManager iconManager, Player player, LangType langType) {

        double taxedPart = amount * taxPercentage;

        return iconManager.get(IconKey.PROPERTY_BUY_TRANSACTION)
                .setName(Lang.SELL_PROPERTY_TRANSACTION.get(langType))
                .setDescription(
                        Lang.TRANSACTION_FROM.get(getPlayerName(buyerID, langType)),
                        Lang.TRANSACTION_TO.get(getColoredName(sellerID, langType)), // Owner can be a territory or a player
                        Lang.TRANSACTION_PROPERTY.get(getPropertyName(territoryID, propertyID, langType)),
                        Lang.TRANSACTION_AMOUNT.get(Double.toString(amount)),
                        Lang.TRANSACTION_TAX.get(Double.toString(taxPercentage), Double.toString(taxedPart)),
                        Lang.TRANSACTION_DATE.get(DateUtil.getRelativeTimeDescription(langType, getDate()))
                )
                .asGuiItem(player, langType);
    }

    @Override
    public String getInsertSQL() {
        return "INSERT INTO transaction_selling_property (timestamp, territory_id, property_id, seller_id, buyer_id, tax_percentage, amount) VALUES (?, ?, ?, ?, ?, ?, ?)";
    }

    @Override
    public void fillInsertStatement(PreparedStatement ps) throws SQLException {
        ps.setLong(1, getDate());
        ps.setString(2, territoryID);
        ps.setString(3, propertyID);
        ps.setString(4, sellerID);
        ps.setString(5, buyerID);
        ps.setDouble(6, taxPercentage);
        ps.setDouble(7, amount);
    }

    @Override
    public Set<String> getConcerned() {
        Set<String> res = new HashSet<>();
        res.add(territoryID);
        res.add(sellerID);
        res.add(buyerID);
        return res;
    }

}
