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
import java.util.HashSet;
import java.util.Set;

public class CreatingPropertyTransaction extends AbstractTransaction {


    private final String territoryID;
    private final String propertyID;
    private final String creatorID;
    private final double amount;
    private final double taxPerBlock;

    public CreatingPropertyTransaction(
            String territoryID,
            String propertyID,
            String creatorID,
            double amount,
            double taxPerBlock
    ) {
        this.territoryID = territoryID;
        this.propertyID = propertyID;
        this.creatorID = creatorID;
        this.amount = amount;
        this.taxPerBlock = taxPerBlock;
    }

    public CreatingPropertyTransaction(ResultSet rs) throws SQLException {
        long timestamp = rs.getLong("timestamp");
        this.localDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
        this.territoryID = rs.getString("territory_id");
        this.propertyID = rs.getString("property_id");
        this.creatorID = rs.getString("creator_id");
        this.amount = rs.getDouble("amount");
        this.taxPerBlock = rs.getInt("tax_per_block");
    }

    public TransactionType getType() {
        return TransactionType.CREATE_PROPERTY;
    }

    @Override
    public GuiItem getIcon(IconManager iconManager, Player player, LangType langType) {


        return iconManager.get(IconKey.PROPERTY_CREATE_TRANSACTION)
                .setName(Lang.SELL_PROPERTY_TRANSACTION.get(langType))
                .setDescription(
                        Lang.TRANSACTION_FROM.get(getColoredName(creatorID, langType)), // Owner can be a territory or a player
                        Lang.TRANSACTION_TO.get(getTerritoryName(territoryID, langType)),
                        Lang.TRANSACTION_PROPERTY.get(getPropertyName(territoryID, propertyID, langType)),
                        Lang.TRANSACTION_AMOUNT.get(Double.toString(amount)),
                        Lang.TRANSACTION_FLAT_TAX.get(Double.toString(taxPerBlock))
                )
                .asGuiItem(player, langType);
    }

    @Override
    public String getInsertSQL() {
        return "INSERT INTO transaction_create_property (timestamp, territory_id, property_id, creator_id, amount, tax_per_block) VALUES (?, ?, ?, ?, ?, ?)";
    }

    @Override
    public void fillInsertStatement(PreparedStatement ps) throws SQLException {
        ps.setLong(1, getDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        ps.setString(2, territoryID);
        ps.setString(3, propertyID);
        ps.setString(4, creatorID);
        ps.setDouble(5, amount);
        ps.setDouble(6, taxPerBlock);
    }

    @Override
    public Set<String> getConcerned() {
        Set<String> res = new HashSet<>();
        res.add(territoryID);
        res.add(creatorID);
        return res;
    }

}
