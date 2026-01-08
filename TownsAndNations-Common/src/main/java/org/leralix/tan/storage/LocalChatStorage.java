package org.leralix.tan.storage;

import org.bukkit.entity.Player;
import org.leralix.lib.data.SoundEnum;
import org.leralix.lib.utils.SoundUtil;
import org.leralix.lib.utils.config.ConfigTag;
import org.leralix.lib.utils.config.ConfigUtil;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.KingdomData;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.enums.ChatScope;
import org.leralix.tan.enums.TownRelation;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.gameplay.TerritoryUtil;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.HashMap;

/**
 * @author leralix
 */
public class LocalChatStorage {
    private static final HashMap<String, ChatScope> playerChatScope = new HashMap<>();

    public static void setPlayerChatScope(Player player, ChatScope scope) {
        playerChatScope.put(player.getUniqueId().toString(), scope);
    }

    public static ChatScope getPlayerChatScope(String uuid) {
        if (!playerChatScope.containsKey(uuid)) {
            return ChatScope.GLOBAL;
        }
        return playerChatScope.get(uuid);
    }

    public static ChatScope getPlayerChatScope(Player player) {
        return getPlayerChatScope(player.getUniqueId().toString());
    }

    public static void removePlayerChatScope(String uuid) {
        playerChatScope.remove(uuid);
    }

    public static void removePlayerChatScope(Player player) {
        removePlayerChatScope(player.getUniqueId().toString());
    }

    public static boolean isPlayerInChatScope(String uuid) {
        return playerChatScope.containsKey(uuid);
    }

     public static void broadcastInScope(Player player, String message) {
        ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(player);
        ChatScope scope = getPlayerChatScope(player);
        boolean sendLogsToConsole = ConfigUtil.getCustomConfig(ConfigTag.MAIN).getBoolean("sendPrivateMessagesToConsole", false);

        switch (scope) {
            case CITY:
                broadcastTownMessage(player, tanPlayer, message, sendLogsToConsole);
                return;
            case REGION:
                broadcastRegionMessage(player, tanPlayer, message, sendLogsToConsole);
                return;
            case KINGDOM:
                broadcastKingdomMessage(player, tanPlayer, message, sendLogsToConsole);
                return;
            case ALLIANCE:
                broadcastAllianceMessage(player, tanPlayer, message, sendLogsToConsole);
                return;
            default:
        }

     }

     private static void broadcastTownMessage(Player player, ITanPlayer tanPlayer, String message, boolean sendLogsToConsole) {
         if (!tanPlayer.hasTown()) {
             TanChatUtils.message(player, Lang.NO_TOWN.get(tanPlayer.getLang()), SoundEnum.NOT_ALLOWED);
             return;
         }

         TownData townData = tanPlayer.getTown();
         FilledLang messageFormat = Lang.CHAT_SCOPE_TOWN_MESSAGE.get(townData.getName(), player.getName(), message);
         townData.broadCastMessage(messageFormat);
         logIfNeeded(messageFormat, sendLogsToConsole);
     }

     private static void broadcastRegionMessage(Player player, ITanPlayer tanPlayer, String message, boolean sendLogsToConsole) {
         if (!tanPlayer.hasRegion()) {
             TanChatUtils.message(player, Lang.NO_REGION.get(tanPlayer.getLang()), SoundEnum.NOT_ALLOWED);
             return;
         }

         RegionData regionData = tanPlayer.getRegion();
         FilledLang messageFormat = Lang.CHAT_SCOPE_REGION_MESSAGE.get(regionData.getName(), player.getName(), message);
         regionData.broadCastMessage(messageFormat);
         logIfNeeded(messageFormat, sendLogsToConsole);
     }

     private static void broadcastKingdomMessage(Player player, ITanPlayer tanPlayer, String message, boolean sendLogsToConsole) {
         if (!tanPlayer.hasKingdom()) {
             TanChatUtils.message(player, Lang.NO_KINGDOM.get(tanPlayer.getLang()), SoundEnum.NOT_ALLOWED);
             return;
         }

         KingdomData kingdomData = tanPlayer.getKingdom();
         FilledLang messageFormat = Lang.CHAT_SCOPE_KINGDOM_MESSAGE.get(kingdomData.getName(), player.getName(), message);
         kingdomData.broadCastMessage(messageFormat);
         logIfNeeded(messageFormat, sendLogsToConsole);
     }

     private static void broadcastAllianceMessage(Player player, ITanPlayer tanPlayer, String message, boolean sendLogsToConsole) {
         if (!tanPlayer.hasTown()) {
             TanChatUtils.message(player, Lang.NO_TOWN.get(tanPlayer.getLang()), SoundEnum.NOT_ALLOWED);
             return;
         }

         TownData playerTown = tanPlayer.getTown();
         FilledLang messageFormat = Lang.CHAT_SCOPE_TOWN_MESSAGE.get(playerTown.getName(), player.getName(), message);
         playerTown.broadCastMessage(messageFormat);
         playerTown.getRelations().getTerritoriesIDWithRelation(TownRelation.ALLIANCE)
                 .forEach(territoryID -> TerritoryUtil.getTerritory(territoryID)
                         .broadCastMessage(Lang.CHAT_SCOPE_ALLIANCE_MESSAGE.get(playerTown.getName(), player.getName(), message)));
         logIfNeeded(messageFormat, sendLogsToConsole);
     }

     private static void logIfNeeded(FilledLang message, boolean sendLogsToConsole) {
         if (sendLogsToConsole) {
             TownsAndNations.getPlugin().getLogger().info(message.getDefault());
         }
     }


 }
