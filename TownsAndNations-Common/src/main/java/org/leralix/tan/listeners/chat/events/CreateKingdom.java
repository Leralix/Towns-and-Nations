package org.leralix.tan.listeners.chat.events;

import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.KingdomData;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.events.EventManager;
import org.leralix.tan.events.events.KingdomCreatedInternalEvent;
import org.leralix.tan.gui.legacy.PlayerGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.chat.ChatListenerEvent;
import org.leralix.tan.storage.stored.KingdomDataStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.file.FileUtil;
import org.leralix.tan.utils.text.TanChatUtils;

public class CreateKingdom extends ChatListenerEvent {

    private final int cost;

    public CreateKingdom(int cost) {
        super();
        this.cost = cost;
    }

    @Override
    public boolean execute(Player player, String message) {

        ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(player);

        if (!tanPlayer.hasRegion()) {
            TanChatUtils.message(player, Lang.PLAYER_NO_REGION.get(tanPlayer));
            return false;
        }

        RegionData regionData = tanPlayer.getRegion();

        if (!regionData.isLeader(tanPlayer)) {
            TanChatUtils.message(player, Lang.PLAYER_ONLY_LEADER_CAN_PERFORM_ACTION.get(tanPlayer));
            return false;
        }

        if (regionData.getBalance() < cost) {
            TanChatUtils.message(player, Lang.TERRITORY_NOT_ENOUGH_MONEY.get(tanPlayer, regionData.getColoredName(), Double.toString(cost - regionData.getBalance())));
            return false;
        }

        int maxSize = Constants.getKingdomMaxNameSize();
        if (message.length() > maxSize) {
            TanChatUtils.message(player, Lang.MESSAGE_TOO_LONG.get(tanPlayer, Integer.toString(maxSize)));
            return false;
        }

        if (KingdomDataStorage.getInstance().isNameUsed(message)) {
            TanChatUtils.message(player, Lang.NAME_ALREADY_USED.get(tanPlayer));
            return false;
        }

        createKingdom(player, message, regionData);
        return true;
    }

    private void createKingdom(Player player, String kingdomName, RegionData capital) {
        capital.removeFromBalance(cost);
        KingdomData kingdom = KingdomDataStorage.getInstance().createNewKingdom(kingdomName, capital);

        ITanPlayer playerData = PlayerDataStorage.getInstance().get(player);
        EventManager.getInstance().callEvent(new KingdomCreatedInternalEvent(kingdom, playerData));
        FileUtil.addLineToHistory(Lang.KINGDOM_CREATED_NEWSLETTER.get(player.getName(), kingdom.getName()));

        openGui(p -> PlayerGUI.dispatchPlayerKingdom(player), player);
    }
}
