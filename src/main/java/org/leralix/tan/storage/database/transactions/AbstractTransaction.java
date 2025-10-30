package org.leralix.tan.storage.database.transactions;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.leralix.tan.gui.cosmetic.IconManager;
import org.leralix.tan.lang.LangType;

import java.time.LocalDateTime;

public abstract class AbstractTransaction {

    protected final LocalDateTime localDate;

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

}
