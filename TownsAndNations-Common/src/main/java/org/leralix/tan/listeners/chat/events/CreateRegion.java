package org.leralix.tan.listeners.chat.events;

import org.bukkit.entity.Player;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.RegionData;
import org.leralix.tan.data.territory.TownData;
import org.leralix.tan.events.EventManager;
import org.leralix.tan.events.events.RegionCreatedInternalEvent;
import org.leralix.tan.gui.legacy.PlayerGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.chat.ChatListenerEvent;
import org.leralix.tan.storage.stored.RegionDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.constants.Constants;
import org.leralix.tan.utils.text.NameFilter;
import org.leralix.tan.utils.text.TanChatUtils;

public class CreateRegion extends ChatListenerEvent {

    private final int cost;

    public CreateRegion(int cost) {
        super();
        this.cost = cost;
    }

    @Override
    public boolean execute(Player player, ITanPlayer playerData, String message) {

        TownData town = TownDataStorage.getInstance().get(playerData);

        if(!town.isLeader(player)){
            TanChatUtils.message(player, Lang.PLAYER_ONLY_LEADER_CAN_PERFORM_ACTION.get(playerData));
            return false;
        }

        if(town.getBalance() < cost){
            TanChatUtils.message(player, Lang.TERRITORY_NOT_ENOUGH_MONEY.get(playerData, town.getColoredName(), Double.toString(cost - town.getBalance())));
            return false;
        }

        String regionName = message == null ? "" : message.trim();

        if (!NameFilter.validateOrWarn(player, regionName, NameFilter.Scope.REGION)) {
            return false;
        }

        int maxSize = Constants.getRegionMaxNameSize();
        if(regionName.length() > maxSize){
            TanChatUtils.message(player, Lang.MESSAGE_TOO_LONG.get(playerData, Integer.toString(maxSize)));
            return false;
        }

        if(RegionDataStorage.getInstance().isNameUsed(regionName)){
            TanChatUtils.message(player, Lang.NAME_ALREADY_USED.get(playerData));
            return false;
        }

        createRegion(player, playerData, regionName, town);
        return true;
    }

    private void createRegion(Player player, ITanPlayer playerData, String regionName, TownData capital) {
        capital.removeFromBalance(cost);
        RegionData newRegion = RegionDataStorage.getInstance().createNewRegion(regionName, capital);

        EventManager.getInstance().callEvent(new RegionCreatedInternalEvent(newRegion, playerData));

        openGui(p -> PlayerGUI.dispatchPlayerRegion(player, playerData), player);
    }
}
