package org.leralix.tan.listeners.chat.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.gui.PlayerGUI;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.listeners.chat.ChatListenerEvent;
import org.leralix.tan.listeners.chat.PlayerChatListenerStorage;
import org.leralix.tan.storage.stored.RegionDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.TanChatUtils;

public class CreateRegion extends ChatListenerEvent {

    private final int cost;

    public CreateRegion(int cost) {
        super();
        this.cost = cost;

    }

    @Override
    public void execute(Player player, String message) {


        TownData town = TownDataStorage.getInstance().get(player);

        if(!town.isLeader(player)){
            player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_ONLY_LEADER_CAN_PERFORM_ACTION.get());
            return;
        }

        if(town.getBalance() < cost){
            player.sendMessage(TanChatUtils.getTANString() + Lang.TOWN_NOT_ENOUGH_MONEY.get());
            return;
        }

        int maxSize = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getInt("RegionNameSize");
        if(message.length() > maxSize){
            player.sendMessage(TanChatUtils.getTANString() + Lang.MESSAGE_TOO_LONG.get(maxSize));
            return;
        }

        if(RegionDataStorage.getInstance().isNameUsed(message)){
            player.sendMessage(TanChatUtils.getTANString() + Lang.NAME_ALREADY_USED.get());
            return;
        }

        createRegion(player, message, town);
    }

    private void createRegion(Player player, String regionName, TownData capital) {
        capital.removeFromBalance(cost);
        RegionDataStorage.getInstance().createNewRegion(regionName, capital);
        Bukkit.broadcastMessage(TanChatUtils.getTANString() + Lang.REGION_CREATE_SUCCESS_BROADCAST.get(capital.getColoredName(),regionName));
        PlayerChatListenerStorage.removePlayer(player);

        openGui(p -> PlayerGUI.dispatchPlayerRegion(player), player);
    }
}
