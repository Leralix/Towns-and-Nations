package org.leralix.tan.storage.database.transactions.instance.daily;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.gui.cosmetic.type.IconBuilder;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.database.transactions.TransactionType;
import org.leralix.tan.storage.database.transactions.instance.SalaryTransaction;
import org.leralix.tan.utils.text.DateUtil;

import java.util.Collection;
import java.util.List;

public class DailySalaryTransaction extends AbstractDailyTransaction {

    private final List<SalaryTransaction> transactions;
    private final double totalAmount;

    public DailySalaryTransaction(List<SalaryTransaction> transactions) {
        super(transactions.getFirst().getDate());
        this.transactions = transactions;
        this.totalAmount = transactions.stream().mapToDouble(SalaryTransaction::getAmount).sum();
    }

    @Override
    public TransactionType getType() {
        return TransactionType.SALARY;
    }

    @Override
    public GuiItem getIcon(IconManager iconManager, Player player, LangType langType) {
        IconBuilder icon = iconManager.get(IconKey.SALARY_TRANSACTION)
                .setName(Lang.SALARY_TRANSACTION_NAME.get(langType))
                .setDescription(
                        Lang.TRANSACTION_DATE.get(DateUtil.getRelativeTimeDescription(langType, getDate())),
                        Lang.TRANSACTION_AMOUNT.get(Double.toString(totalAmount))
                );
        for(SalaryTransaction transaction : getPartTransactionlistIfNecessary(transactions)) {
            icon.addDescription(transaction.getDailyLine(langType));
        }
        return icon.asGuiItem(player, langType);
    }

    private Collection<SalaryTransaction> getPartTransactionlistIfNecessary(List<SalaryTransaction> transactions) {
        if(transactions.size() > 15) {
            return transactions.subList(0, 15);
        }
        return transactions;
    }
}
