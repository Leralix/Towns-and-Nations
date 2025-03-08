package org.leralix.tan.storage;

import org.bukkit.entity.Player;
import org.leralix.tan.utils.TerritoryUtil;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.dataclass.PlayerData;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.enums.ChatScope;
import org.leralix.tan.enums.TownRelation;

import java.util.HashMap;

public class LocalChatStorage {
    private static final HashMap<String, ChatScope> playerChatScope = new HashMap<>();

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
            if(townData != null)
                townData.broadCastMessage(Lang.CHAT_SCOPE_TOWN_MESSAGE.get(townData.getName(),player.getName(),message));
        }

        else if(scope == ChatScope.REGION){
            RegionData regionData = playerData.getRegion();
            if(regionData != null)
                regionData.broadCastMessage(Lang.CHAT_SCOPE_REGION_MESSAGE.get(regionData.getName(),player.getName(),message));
        }

        else if(scope == ChatScope.ALLIANCE){
            TownData playerTown = playerData.getTown();

            playerTown.broadCastMessage(Lang.CHAT_SCOPE_TOWN_MESSAGE.get(playerTown.getName(),player.getName(),message));
            playerTown.getRelations().getTerritoriesIDWithRelation(TownRelation.ALLIANCE).forEach(territoryID -> TerritoryUtil.getTerritory(territoryID).broadCastMessage(Lang.CHAT_SCOPE_ALLIANCE_MESSAGE.get(playerTown.getName(),player.getName(),message)));

        }

    }


}
