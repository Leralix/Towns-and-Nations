package org.leralix.tan.commands.player;

import org.bukkit.entity.Player;
import org.leralix.lib.commands.PlayerSubCommand;
import org.leralix.lib.data.SoundEnum;
import org.leralix.tan.data.player.ITanPlayer;
import org.leralix.tan.data.territory.NationData;
import org.leralix.tan.data.territory.RegionData;
import org.leralix.tan.data.territory.TownData;
import org.leralix.tan.data.territory.relation.TownRelation;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.LocalChatStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.gameplay.TerritoryUtil;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ChannelChatScopeCommand extends PlayerSubCommand {

    private static final String TOWN = "town";
    private static final String ALLIANCE = "alliance";
    private static final String REGION = "region";
    private static final String NATION = "nation";
    private static final String GLOBAL = "global";

    @Override
    public String getName() {
        return "chat";
    }

    @Override
    public String getDescription() {
        return Lang.TOWN_CHAT_COMMAND_DESC.getDefault();
    }

    public int getArguments() {
        return 1;
    }

    @Override
    public String getSyntax() {
        return "/tan chat <global|alliance|nation|region|town> [message]";
    }

    @Override
    public List<String> getTabCompleteSuggestions(Player player, String lowerCase, String[] args) {
        List<String> suggestions = new ArrayList<>();
        if (args.length == 2) {
            suggestions.add(GLOBAL);
            suggestions.add(TOWN);
            suggestions.add(ALLIANCE);
            suggestions.add(REGION);
            suggestions.add(NATION);
        }
        return suggestions;
    }

    @Override
    public void perform(Player player, String[] args) {
        LangType langType = PlayerDataStorage.getInstance().get(player).getLang();
        if (args.length < 2) {
            TanChatUtils.message(player, Lang.NOT_ENOUGH_ARGS_ERROR.get(langType), SoundEnum.NOT_ALLOWED);
            TanChatUtils.message(player, Lang.CORRECT_SYNTAX_INFO.get(langType, getSyntax()));
            return;
        }
        if (args.length == 2) {
            registerPlayerToScope(player, args[1]);
            return;
        }

        sendSingleMessage(player, args[1], args);
    }

    private static void registerPlayerToScope(Player player, String channelName) {
        ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(player);
        LangType langType = tanPlayer.getLang();
        ChatScope currentScope = LocalChatStorage.getPlayerChatScope(player);
        String normalizedChannelName = channelName.toLowerCase();
        switch (normalizedChannelName) {
            case GLOBAL:
                LocalChatStorage.removePlayerChatScope(player);
                TanChatUtils.message(player, Lang.CHAT_CHANGED.get(langType, channelName));
                return;
            case TOWN:
                setScope(player, langType, channelName, currentScope, ChatScope.CITY);
                return;
            case ALLIANCE:
                if (!tanPlayer.hasTown()) {
                    TanChatUtils.message(player, Lang.PLAYER_NO_TOWN.get(langType));
                    return;
                }
                setScope(player, langType, channelName, currentScope, ChatScope.ALLIANCE);
                return;
            case REGION:
                if (!tanPlayer.hasRegion()) {
                    TanChatUtils.message(player, Lang.PLAYER_NO_REGION.get(langType));
                    return;
                }
                setScope(player, langType, channelName, currentScope, ChatScope.REGION);
                return;
            case NATION:
                if (!tanPlayer.hasNation()) {
                    TanChatUtils.message(player, Lang.PLAYER_NO_NATION.get(langType));
                    return;
                }
                setScope(player, langType, channelName, currentScope, ChatScope.NATION);
                return;
            default:
                TanChatUtils.message(player, Lang.CHAT_SCOPE_NOT_FOUND.get(langType, channelName));
        }
    }

    private static void setScope(Player player, LangType langType, String channelName, ChatScope currentScope, ChatScope wantedScope) {
        if (currentScope == wantedScope) {
            TanChatUtils.message(player, Lang.TOWN_CHAT_ALREADY_IN_CHAT.get(langType, wantedScope.getName(langType)));
            return;
        }
        LocalChatStorage.setPlayerChatScope(player, wantedScope);
        TanChatUtils.message(player, Lang.CHAT_CHANGED.get(langType, channelName));
    }

    private void sendSingleMessage(Player player, String channelName, String[] words) {
        ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(player);
        LangType langType = tanPlayer.getLang();
        String message = String.join(" ", Arrays.copyOfRange(words, 2, words.length));

        String normalizedChannelName = channelName.toLowerCase();
        switch (normalizedChannelName) {
            case GLOBAL:
                sendGlobalMessage(player, message);
                return;
            case ALLIANCE:
                sendAllianceMessage(player, tanPlayer, langType, message);
                return;
            case REGION:
                sendRegionMessage(player, tanPlayer, langType, message);
                return;
            case NATION:
                sendNationMessage(player, tanPlayer, langType, message);
                return;
            case TOWN:
                sendTownMessage(player, tanPlayer, langType, message);
                return;
            default:
                TanChatUtils.message(player, Lang.CORRECT_SYNTAX_INFO.get(langType, getSyntax()));
        }
    }

    private static void sendGlobalMessage(Player player, String message) {
        if (!LocalChatStorage.isPlayerInChatScope(player.getUniqueId().toString())) {
            player.chat(message);
            return;
        }

        ChatScope prevScope = LocalChatStorage.getPlayerChatScope(player);
        LocalChatStorage.removePlayerChatScope(player);
        player.chat(message);
        LocalChatStorage.setPlayerChatScope(player, prevScope);
    }

    private static void sendAllianceMessage(Player player, ITanPlayer tanPlayer, LangType langType, String message) {
        if (!tanPlayer.hasTown()) {
            TanChatUtils.message(player, Lang.PLAYER_NO_TOWN.get(langType));
            return;
        }

        TownData playerTown = tanPlayer.getTown();
        if (playerTown == null) {
            return;
        }

        playerTown.getRelations().getTerritoriesIDWithRelation(TownRelation.ALLIANCE)
                .forEach(territoryID -> Objects.requireNonNull(TerritoryUtil.getTerritory(territoryID))
                        .broadCastMessage(Lang.CHAT_SCOPE_ALLIANCE_MESSAGE.get(playerTown.getName(), player.getName(), message))
                );
    }

    private static void sendRegionMessage(Player player, ITanPlayer tanPlayer, LangType langType, String message) {
        if (!tanPlayer.hasRegion()) {
            TanChatUtils.message(player, Lang.PLAYER_NO_REGION.get(langType));
            return;
        }

        RegionData regionData = tanPlayer.getRegion();
        if (regionData == null) {
            return;
        }

        regionData.broadCastMessage(Lang.CHAT_SCOPE_REGION_MESSAGE.get(regionData.getName(), player.getName(), message));
    }

    private static void sendNationMessage(Player player, ITanPlayer tanPlayer, LangType langType, String message) {
        if (!tanPlayer.hasNation()) {
            TanChatUtils.message(player, Lang.PLAYER_NO_NATION.get(langType));
            return;
        }

        NationData nationData = tanPlayer.getNation();
        if (nationData == null) {
            return;
        }

        nationData.broadCastMessage(Lang.CHAT_SCOPE_NATION_MESSAGE.get(nationData.getName(), player.getName(), message));
    }

    private static void sendTownMessage(Player player, ITanPlayer tanPlayer, LangType langType, String message) {
        if (!tanPlayer.hasTown()) {
            TanChatUtils.message(player, Lang.PLAYER_NO_TOWN.get(langType));
            return;
        }

        TownData townData = tanPlayer.getTown();
        if (townData == null) {
            return;
        }

        townData.broadCastMessage(Lang.CHAT_SCOPE_TOWN_MESSAGE.get(townData.getName(), player.getName(), message));
    }
}
