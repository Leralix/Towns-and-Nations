package org.leralix.tan.gui.user.territory.history;

import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.storage.database.transactions.EntityScope;
import org.leralix.tan.storage.database.transactions.TransactionType;

import java.util.function.Consumer;

public class PlayerTransactionHistory extends AbstractTransactionHistory {

    private final ITanPlayer playerData;

    public PlayerTransactionHistory(
            Player player,
            ITanPlayer playerData,
            Consumer<Player> guiCallback
    ){
        super(player, guiCallback);
        this.playerData = playerData;
        open();
    }

    public PlayerTransactionHistory(
            Player player,
            ITanPlayer playerData,
            TransactionType transactionType,
            Consumer<Player> guiCallback
    ){
        super(player, guiCallback, transactionType);
        this.playerData = playerData;
    }


    @Override
    protected EntityScope getScope() {
        return EntityScope.PLAYER;
    }

    @Override
    protected String getID() {
        return playerData.getID();
    }
}
