package org.leralix.tan.listeners.chat.events;

import org.bukkit.entity.Player;
import org.leralix.lib.data.SoundEnum;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.tan.economy.EconomyUtil;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.chat.ChatListenerEvent;
import org.leralix.tan.storage.database.transactions.TransactionManager;
import org.leralix.tan.storage.database.transactions.instance.RetrieveTransaction;
import org.leralix.tan.utils.text.TanChatUtils;

public class RetrieveMoney extends ChatListenerEvent {
    TerritoryData territoryData;
    public RetrieveMoney(TerritoryData territoryData) {
        this.territoryData = territoryData;
    }

    @Override
    public boolean execute(Player player, String message) {

        Double amount = parseStringToDouble(message);
        if(amount == null || amount <= 0){
            TanChatUtils.message(player, Lang.SYNTAX_ERROR_AMOUNT.get(player));
            return false;
        }

        if(amount > territoryData.getBalance()){
            TanChatUtils.message(player, Lang.TERRITORY_NOT_ENOUGH_MONEY.get(player, territoryData.getColoredName(), Double.toString(amount - territoryData.getBalance())));
            return false;
        }
        territoryData.removeFromBalance(amount);
        EconomyUtil.addFromBalance(player, amount);

        TanChatUtils.message(player, Lang.TOWN_RETRIEVE_MONEY_SUCCESS.get(player, Double.toString(amount)), SoundEnum.MINOR_GOOD);
        TransactionManager.getInstance().register(new RetrieveTransaction(territoryData, player, amount));
        return true;
    }
}
