package org.leralix.tan.storage.database.transactions.instance;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.gui.cosmetic.type.IconBuilder;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.database.transactions.AbstractTransaction;
import org.leralix.tan.storage.database.transactions.TransactionType;
import org.leralix.tan.utils.text.DateUtil;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class DailyPlayerTaxTransaction extends AbstractTransaction {

    private final List<PlayerTaxTransaction> transactions;
    private final double totalAmount;

    public DailyPlayerTaxTransaction(List<PlayerTaxTransaction> transactions) {
        super(transactions.getFirst().getDate());
        this.transactions = transactions;
        this.totalAmount = transactions.stream().mapToDouble(PlayerTaxTransaction::getAmount).sum();
    }

    @Override
    public TransactionType getType() {
        return TransactionType.TAXES;
    }

    @Override
    public GuiItem getIcon(IconManager iconManager, Player player, LangType langType) {
        IconBuilder icon = iconManager.get(IconKey.PLAYER_TAX_TRANSACTION)
                .setName(Lang.PLAYER_TAX_TRANSACTION.get(langType))
                .setDescription(
                        Lang.TRANSACTION_DATE.get(DateUtil.getRelativeTimeDescription(langType, getDate())),
                        Lang.TRANSACTION_AMOUNT.get(Double.toString(totalAmount))
                );

        for(PlayerTaxTransaction transaction : transactions) {
            icon.addDescription(transaction.getDailyLine(langType));
        }
        return icon.asGuiItem(player, langType);
    }

    @Override
    public String getInsertSQL() {
        return null; // Not stored in DB
    }

    @Override
    public void fillInsertStatement(PreparedStatement ps) throws SQLException {
        // Not stored in DB
    }

    @Override
    public Set<String> getConcerned() {
        return Collections.emptySet();
    }
}
