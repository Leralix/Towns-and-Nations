package org.leralix.tan.gui.user.territory.history;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.gui.IteratorGUI;
import org.leralix.tan.gui.cosmetic.IconKey;
import org.leralix.tan.gui.user.territory.TreasuryMenu;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.database.transactions.AbstractTransaction;
import org.leralix.tan.storage.database.transactions.EntityScope;
import org.leralix.tan.storage.database.transactions.TransactionManager;
import org.leralix.tan.storage.database.transactions.TransactionType;

import java.util.ArrayList;
import java.util.List;

public class TerritoryTransactionHistory extends IteratorGUI {

    private final TerritoryData territoryData;
    private TransactionType transactionType;

    public TerritoryTransactionHistory(Player player, TerritoryData territoryData){
        this(player, territoryData, TransactionType.INDEX);
    }

    public TerritoryTransactionHistory(Player player, TerritoryData territoryData, TransactionType transactionType){
        super(player, Lang.HEADER_HISTORY, 6);
        this.territoryData = territoryData;
        this.transactionType = transactionType;
        open();
    }


    @Override
    public void open() {
        iterator(getTransactions(), p -> new TreasuryMenu(player, territoryData));
        gui.setItem(6, 5, getNextScopeButton());
        gui.open(player);
    }

    private @NotNull GuiItem getNextScopeButton() {
        return iconManager.get(IconKey.CHANGE_SCOPE_ICON)
                .setName(Lang.BROWSE_SCOPE.get(langType, transactionType.getName(langType)))
                .setDescription(Lang.LEFT_CLICK_TO_MODIFY.get())
                .setAction( action -> {
                    transactionType = transactionType.next(EntityScope.TERRITORY);
                    SoundUtil.playSound(player, SoundEnum.ADD);
                    open();
                })
                .asGuiItem(player, langType);
    }

    private List<GuiItem> getTransactions() {

        List<GuiItem> res = new ArrayList<>();
        var transactions = TransactionManager.getInstance().getTransactionOf(territoryData.getID(), transactionType);

        for(AbstractTransaction transaction : transactions){
            res.add(transaction.getIcon(iconManager, player, langType));
        }
        return res;
    }
}
