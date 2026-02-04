package org.leralix.tan.storage;

import org.bukkit.entity.Player;
import org.leralix.lib.data.SoundEnum;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.commands.player.ChatScope;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.NationData;
import org.leralix.tan.data.territory.RegionData;
import org.leralix.tan.data.territory.TownData;
import org.leralix.tan.data.territory.relation.TownRelation;
import org.leralix.tan.lang.FilledLang;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.gameplay.TerritoryUtil;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.HashMap;

public class LocalChatStorage {

    private final PlayerDataStorage playerDataStorage;
    private final boolean sendMessageToConsole;

    public LocalChatStorage(PlayerDataStorage playerDataStorage, boolean sendMessageToConsole) {
        this.playerDataStorage = playerDataStorage;
        this.sendMessageToConsole = sendMessageToConsole;
    }


    private final HashMap<String, ChatScope> playerChatScope = new HashMap<>();

    public void setPlayerChatScope(Player player, ChatScope scope) {
        playerChatScope.put(player.getUniqueId().toString(), scope);
    }

    public ChatScope getPlayerChatScope(String uuid) {
        if (!playerChatScope.containsKey(uuid)) {
            return ChatScope.GLOBAL;
        }
        return playerChatScope.get(uuid);
    }

    public ChatScope getPlayerChatScope(Player player) {
        return getPlayerChatScope(player.getUniqueId().toString());
    }

    public void removePlayerChatScope(String uuid) {
        playerChatScope.remove(uuid);
    }

    public void removePlayerChatScope(Player player) {
        removePlayerChatScope(player.getUniqueId().toString());
    }

    public boolean isPlayerInChatScope(String uuid) {
        return playerChatScope.containsKey(uuid);
    }

    public void broadcastInScope(Player player, String message) {
        ITanPlayer tanPlayer = playerDataStorage.get(player);
        ChatScope scope = getPlayerChatScope(player);
        switch (scope) {
            case CITY:
                broadcastTownMessage(player, tanPlayer, message, sendMessageToConsole);
                return;
            case REGION:
                broadcastRegionMessage(player, tanPlayer, message, sendMessageToConsole);
                return;
            case NATION:
                broadcastNationMessage(player, tanPlayer, message, sendMessageToConsole);
                return;
            case ALLIANCE:
                broadcastAllianceMessage(player, tanPlayer, message, sendMessageToConsole);
                return;
            default:
        }

    }

    private void broadcastTownMessage(Player player, ITanPlayer tanPlayer, String message, boolean sendLogsToConsole) {
        if (!tanPlayer.hasTown()) {
            TanChatUtils.message(player, Lang.NO_TOWN.get(tanPlayer.getLang()), SoundEnum.NOT_ALLOWED);
            return;
        }

        TownData townData = tanPlayer.getTown();
        FilledLang messageFormat = Lang.CHAT_SCOPE_TOWN_MESSAGE.get(townData.getName(), player.getName(), message);
        townData.broadCastMessage(messageFormat);
        logIfNeeded(messageFormat, sendLogsToConsole);
    }

    private void broadcastRegionMessage(Player player, ITanPlayer tanPlayer, String message, boolean sendLogsToConsole) {
        if (!tanPlayer.hasRegion()) {
            TanChatUtils.message(player, Lang.NO_REGION.get(tanPlayer.getLang()), SoundEnum.NOT_ALLOWED);
            return;
        }

        RegionData regionData = tanPlayer.getRegion();
        FilledLang messageFormat = Lang.CHAT_SCOPE_REGION_MESSAGE.get(regionData.getName(), player.getName(), message);
        regionData.broadCastMessage(messageFormat);
        logIfNeeded(messageFormat, sendLogsToConsole);
    }

    private void broadcastNationMessage(Player player, ITanPlayer tanPlayer, String message, boolean sendLogsToConsole) {
        if (!tanPlayer.hasNation()) {
            TanChatUtils.message(player, Lang.NO_NATION.get(tanPlayer.getLang()), SoundEnum.NOT_ALLOWED);
            return;
        }

        NationData nationData = tanPlayer.getNation();
        FilledLang messageFormat = Lang.CHAT_SCOPE_NATION_MESSAGE.get(nationData.getName(), player.getName(), message);
        nationData.broadCastMessage(messageFormat);
        logIfNeeded(messageFormat, sendLogsToConsole);
    }

    private void broadcastAllianceMessage(Player player, ITanPlayer tanPlayer, String message, boolean sendLogsToConsole) {
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

    private void logIfNeeded(FilledLang message, boolean sendLogsToConsole) {
        if (sendLogsToConsole) {
            TownsAndNations.getPlugin().getLogger().info(message.getDefault());
        }
    }


}
