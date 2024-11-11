package org.leralix.tan.listeners.chatlistener.events;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.leralix.tan.dataclass.PlayerData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.economy.EconomyUtil;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.listeners.chatlistener.ChatListenerEvent;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.ChatUtils;
import org.leralix.tan.utils.FileUtil;
import org.leralix.tan.utils.SoundUtil;
import org.leralix.tan.utils.config.ConfigTag;
import org.leralix.tan.utils.config.ConfigUtil;

import static org.leralix.tan.enums.SoundEnum.LEVEL_UP;
import static org.leralix.tan.listeners.chatlistener.PlayerChatListenerStorage.removePlayer;
import static org.leralix.tan.utils.TeamUtils.setIndividualScoreBoard;

public class CreateTown extends ChatListenerEvent {
    int cost;
    public CreateTown(int cost) {
        super();
        this.cost = cost;
    }

    @Override
    public void execute(Player player, String message) {
        PlayerData playerData = PlayerDataStorage.get(player);
        double playerBalance = EconomyUtil.getBalance(player);

        if(playerBalance < cost){
            player.sendMessage(Lang.PLAYER_NOT_ENOUGH_MONEY_EXTENDED.get(cost - playerBalance));
            return;
        }

        FileConfiguration config =  ConfigUtil.getCustomConfig(ConfigTag.MAIN);
        int maxSize = config.getInt("TownNameSize");

        if(message.length() > maxSize){
            player.sendMessage(ChatUtils.getTANString() + Lang.MESSAGE_TOO_LONG.get(maxSize));
            return;
        }

        if(TownDataStorage.isNameUsed(message)){
            player.sendMessage(ChatUtils.getTANString() + Lang.NAME_ALREADY_USED.get());
            return;
        }

        TownData newTown = TownDataStorage.newTown(message,player);
        EconomyUtil.removeFromBalance(player,cost);
        playerData.joinTown(newTown);



        Bukkit.broadcastMessage(ChatUtils.getTANString() + Lang.TOWN_CREATE_SUCCESS_BROADCAST.get(player.getName(),message));
        SoundUtil.playSound(player, LEVEL_UP);
        removePlayer(player);
        FileUtil.addLineToHistory(Lang.HISTORY_TOWN_CREATED.get(player.getName(),message));

        Bukkit.getScheduler().runTask(TownsAndNations.getPlugin(), () -> setIndividualScoreBoard(player));

    }
}
