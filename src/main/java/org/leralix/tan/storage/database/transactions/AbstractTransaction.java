package org.leralix.tan.storage.database.transactions;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.lang.LangType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public abstract class AbstractTransaction {

    protected LocalDateTime localDate;

    protected AbstractTransaction(){
        this.localDate = LocalDateTime.now();
    }

    protected AbstractTransaction(LocalDateTime localDate){
        this.localDate = localDate;
    }

    public LocalDateTime getDate(){
        return localDate;
    }

    public abstract TransactionType getType();

    public abstract GuiItem getIcon(IconManager iconManager, Player player, LangType langType);

    /** Each subclass must define how to load itself from a ResultSet */
    protected abstract void fromResultSet(ResultSet rs) throws SQLException;

    public abstract String getInsertSQL();

    /** Factory method for reflective instantiation */
    public static <T extends AbstractTransaction> T fromResultSet(ResultSet rs, Class<T> clazz) throws SQLException {
        try {
            T instance = clazz.getDeclaredConstructor().newInstance();
            instance.fromResultSet(rs);
            return instance;
        } catch (ReflectiveOperationException e) {
            throw new SQLException("Failed to instantiate " + clazz.getSimpleName(), e);
        }
    }

    public abstract void fillInsertStatement(PreparedStatement ps) throws SQLException;

    public abstract List<String> getConcerned();
}
