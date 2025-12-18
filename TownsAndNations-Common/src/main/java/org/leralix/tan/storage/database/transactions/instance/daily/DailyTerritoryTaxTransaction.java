package org.leralix.tan.storage.database.transactions.instance.daily;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.gui.cosmetic.type.IconBuilder;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.database.transactions.TransactionType;
import org.leralix.tan.storage.database.transactions.instance.TerritoryTaxTransaction;
import org.leralix.tan.utils.text.DateUtil;

import java.util.Collection;
import java.util.List;

public class DailyTerritoryTaxTransaction extends AbstractDailyTransaction {

    private final List<TerritoryTaxTransaction> transactions;
    private final double totalAmount;

    public DailyTerritoryTaxTransaction(List<TerritoryTaxTransaction> transactions) {
        super(transactions.getFirst().getDate());
        this.transactions = transactions;
        this.totalAmount = transactions.stream().mapToDouble(TerritoryTaxTransaction::getAmount).sum();
    }

    @Override
    public TransactionType getType() {
        return TransactionType.TERRITORY_TAX;
    }

    @Override
    public GuiItem getIcon(IconManager iconManager, Player player, LangType langType) {
        IconBuilder icon = iconManager.get(IconKey.TERRITORY_TAX_ICON)
                .setName(Lang.PLAYER_TAX_TRANSACTION.get(langType))
                .setDescription(
                        Lang.TRANSACTION_DATE.get(DateUtil.getRelativeTimeDescription(langType, getDate())),
                        Lang.TRANSACTION_AMOUNT.get(Double.toString(totalAmount))
                );
        for(TerritoryTaxTransaction transaction : getPartTransactionlistIfNecessary(transactions)) {
            icon.addDescription(transaction.getDailyLine(langType));
        }
        return icon.asGuiItem(player, langType);
    }

    private Collection<TerritoryTaxTransaction> getPartTransactionlistIfNecessary(List<TerritoryTaxTransaction> transactions) {
        if(transactions.size() > 15) {
            return transactions.subList(0, 15);
        }
        return transactions;
    }
}
