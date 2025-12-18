package org.leralix.tan.storage.database.transactions.instance.daily;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.database.transactions.AbstractTransaction;
import org.leralix.tan.storage.database.transactions.TransactionType;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Set;

public abstract class AbstractDailyTransaction extends AbstractTransaction {

    protected AbstractDailyTransaction(long date){
        super(date);
    }

    @Override
    public abstract TransactionType getType();

    @Override
    public abstract GuiItem getIcon(IconManager iconManager, Player player, LangType langType);

    @Override
    public String getInsertSQL() {
        // Not stored in DB
        return "";
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
