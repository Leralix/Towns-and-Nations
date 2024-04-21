package org.tan.TownsAndNations.storage;

import org.bukkit.entity.Player;
import org.tan.TownsAndNations.DataClass.PlayerData;
import org.tan.TownsAndNations.DataClass.TownData;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.enums.ChatScope;
import org.tan.TownsAndNations.enums.TownRelation;
import org.tan.TownsAndNations.storage.DataStorage.PlayerDataStorage;
import org.tan.TownsAndNations.storage.DataStorage.TownDataStorage;

import java.util.HashMap;

import static org.tan.TownsAndNations.utils.ChatUtils.getTANString;

public class LocalChatStorage {


    private static HashMap<String, ChatScope> playerChatScope = new HashMap<>();


    public void setPlayerChatScope(String uuid, ChatScope scope){
        playerChatScope.put(uuid, scope);
    }

    public static void setPlayerChatScope(Player player, ChatScope scope){
        playerChatScope.put(player.getUniqueId().toString(), scope);
    }

    public static ChatScope getPlayerChatScope(String uuid){
        return playerChatScope.get(uuid);
    }

    public static ChatScope getPlayerChatScope(Player player){
        return getPlayerChatScope(player.getUniqueId().toString());
    }

    public static void removePlayerChatScope(String uuid){
        playerChatScope.remove(uuid);
    }
    public static void removePlayerChatScope(Player player){
        removePlayerChatScope(player.getUniqueId().toString());
    }

    public static boolean isPlayerInChatScope(String uuid){
        return playerChatScope.containsKey(uuid);
    }

    public static void broadcastInScope(Player player, String message){
        PlayerData playerData = PlayerDataStorage.get(player);

        if(!playerData.haveTown()){
            return;
        }

        ChatScope scope = getPlayerChatScope(player);

        if(scope == ChatScope.CITY){
            TownData townData = playerData.getTown();

            townData.broadCastMessage(Lang.CHAT_SCOPE_TOWN_MESSAGE.get(townData.getName(),player.getName(),message));
        }

        else if(scope == ChatScope.ALLIANCE){
            TownData playerTown = playerData.getTown();

            playerTown.broadCastMessage(Lang.CHAT_SCOPE_TOWN_MESSAGE.get(playerTown.getName(),player.getName(),message));

            playerTown.getTownWithRelation(TownRelation.ALLIANCE).forEach(townID -> {
                TownDataStorage.get(townID).broadCastMessage(getTANString() + Lang.CHAT_SCOPE_ALLIANCE_MESSAGE.get(playerTown.getName(),player.getName(),message));
            }
            );

        }

    }


}
