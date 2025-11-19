package org.leralix.tan.gui.user.territory.history;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.database.transactions.AbstractTransaction;
import org.leralix.tan.storage.database.transactions.EntityScope;
import org.leralix.tan.storage.database.transactions.TransactionManager;
import org.leralix.tan.storage.database.transactions.TransactionType;
import org.leralix.tan.utils.deprecated.GuiUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

public abstract class AbstractTransactionHistory extends IteratorGUI {

    private TransactionType transactionType;
    private final Consumer<Player> guiCallback;

    protected AbstractTransactionHistory(Player player, Consumer<Player> guiCallback) {
        this(player, guiCallback, TransactionType.INDEX);
    }

    protected AbstractTransactionHistory(Player player, Consumer<Player> guiCallback, TransactionType transactionType) {
        super(player, Lang.HEADER_HISTORY, 6);
        this.guiCallback = guiCallback;
        this.transactionType = transactionType;
    }


    @Override
    public void open() {
        iterator(getTransactions(), guiCallback);
        gui.setItem(6, 5, getNextScopeButton());
        gui.open(player);
    }

    private @NotNull GuiItem getNextScopeButton() {
        return GuiUtil.getNextScopeButton(
                iconManager,
                this,
                transactionType,
                newValue -> transactionType = newValue,
                langType,
                player
        );
    }

    protected abstract EntityScope getScope();

    private List<GuiItem> getTransactions() {

        List<GuiItem> res = new ArrayList<>();
        var transactions = fetchTransactions();

        for (AbstractTransaction transaction : transactions) {
            res.add(transaction.getIcon(iconManager, player, langType));
        }
        return res;
    }

    protected List<AbstractTransaction> fetchTransactions() {
        var transactions = TransactionManager.getInstance().getTransactionOf(getID(), transactionType);
        // Sort by date ascending
        transactions.sort(Comparator.comparing(AbstractTransaction::getDate).reversed());
        return transactions;
    }

    protected abstract String getID();
}
