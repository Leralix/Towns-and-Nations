package org.leralix.tan.gui.user.territory.history;

import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.storage.database.transactions.EntityScope;
import org.leralix.tan.storage.database.transactions.TransactionType;

import java.util.function.Consumer;

public class TerritoryTransactionHistory extends AbstractTerritoryTransactionHistory {

    private final TerritoryData territoryData;

    public TerritoryTransactionHistory(
            Player player,
            TerritoryData territoryData,
            Consumer<Player> guiCallback
    ){
        super(player, guiCallback);
        this.territoryData = territoryData;
        open();
    }

    public TerritoryTransactionHistory(
            Player player,
            TerritoryData territoryData,
            TransactionType transactionType,
            Consumer<Player> guiCallback
    ){
        super(player, guiCallback, transactionType);
        this.territoryData = territoryData;
        open();
    }


    @Override
    protected EntityScope getScope() {
        return EntityScope.TERRITORY;
    }

    @Override
    protected String getID() {
        return territoryData.getID();
    }
}
