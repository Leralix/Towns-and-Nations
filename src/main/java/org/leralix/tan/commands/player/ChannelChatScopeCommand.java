package org.leralix.tan.commands.player;

import org.bukkit.entity.Player;
import org.leralix.lib.commands.PlayerSubCommand;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.enums.ChatScope;
import org.leralix.tan.enums.TownRelation;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.storage.LocalChatStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tan.utils.TanChatUtils;
import org.leralix.tan.utils.TerritoryUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ChannelChatScopeCommand extends PlayerSubCommand {
    @Override
    public String getName() {
        return "chat";
    }

    @Override
    public String getDescription() {
        return Lang.TOWN_CHAT_COMMAND_DESC.get();
    }

    public int getArguments() {
        return 1;
    }


    @Override
    public String getSyntax() {
        return "/tan chat <global|alliance|region|town> [message]";
    }

    @Override
    public List<String> getTabCompleteSuggestions(Player player, String lowerCase, String[] args) {
        List<String> suggestions = new ArrayList<>();
        if (args.length == 2) {
            suggestions.add("town");
            suggestions.add("alliance");
            suggestions.add("region");
            suggestions.add("global");
        }
        return suggestions;
    }

    @Override
    public void perform(Player player, String[] args) {

        if (args.length == 1) {
            player.sendMessage(TanChatUtils.getTANString() + Lang.NOT_ENOUGH_ARGS_ERROR.get());
            player.sendMessage(TanChatUtils.getTANString() + Lang.CORRECT_SYNTAX_INFO.get(getSyntax()));
        } else if (args.length == 2) {
            registerPlayerToScope(player, args[1]);
        } else if (args.length >= 3) {
            sendSingleMessage(player, args[1], args);
        } else {
            player.sendMessage(TanChatUtils.getTANString() + Lang.TOO_MANY_ARGS_ERROR.get());
            player.sendMessage(TanChatUtils.getTANString() + Lang.CORRECT_SYNTAX_INFO.get(getSyntax()));
        }
    }

    private static void registerPlayerToScope(Player player, String channelName ) {
        TownData town = TownDataStorage.getInstance().get(player);
        if (town == null) {
            player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_NO_TOWN.get());
            return;
        }
        if (channelName.equalsIgnoreCase("global")) {
            LocalChatStorage.removePlayerChatScope(player);
            player.sendMessage(TanChatUtils.getTANString() + Lang.CHAT_CHANGED.get(player, channelName));
            return;
        }
        ChatScope scope = LocalChatStorage.getPlayerChatScope(player);

        if (channelName.equalsIgnoreCase("town")) {

            if (scope == ChatScope.CITY) {
                player.sendMessage(TanChatUtils.getTANString() + Lang.TOWN_CHAT_ALREADY_IN_CHAT.get(ChatScope.CITY.getName()));
                return;
            }

            LocalChatStorage.setPlayerChatScope(player, ChatScope.CITY);
            player.sendMessage(TanChatUtils.getTANString() + Lang.CHAT_CHANGED.get(channelName));
            return;
        }
        if (channelName.equalsIgnoreCase("alliance")) {

            if (scope == ChatScope.ALLIANCE) {
                player.sendMessage(TanChatUtils.getTANString() + Lang.TOWN_CHAT_ALREADY_IN_CHAT.get(ChatScope.ALLIANCE.getName()));
                return;
            }

            LocalChatStorage.setPlayerChatScope(player, ChatScope.ALLIANCE);
            player.sendMessage(TanChatUtils.getTANString() + Lang.CHAT_CHANGED.get(channelName));
            return;
        }
        if (channelName.equalsIgnoreCase("region")) {

            if (scope == ChatScope.REGION) {
                player.sendMessage(TanChatUtils.getTANString() + Lang.TOWN_CHAT_ALREADY_IN_CHAT.get(ChatScope.REGION.getName()));
                return;
            }

            LocalChatStorage.setPlayerChatScope(player, ChatScope.REGION);
            player.sendMessage(TanChatUtils.getTANString() + Lang.CHAT_CHANGED.get(channelName));
            return;
        }
        player.sendMessage(TanChatUtils.getTANString() + Lang.CHAT_SCOPE_NOT_FOUND.get(channelName));
    }

    private void sendSingleMessage(Player player, String channelName, String[] words) {
        ITanPlayer tanPlayer = PlayerDataStorage.getInstance().get(player);
        String message = String.join(" ", Arrays.copyOfRange(words, 2, words.length));

        switch (channelName.toLowerCase()) {
            case "global":
                // Workaround
                if (LocalChatStorage.isPlayerInChatScope(player.getUniqueId().toString())) {
                    ChatScope prevScope = LocalChatStorage.getPlayerChatScope(player);
                    LocalChatStorage.removePlayerChatScope(player);
                    player.chat(message);
                    LocalChatStorage.setPlayerChatScope(player, prevScope);
                } else {
                    player.chat(message);
                }
                return;
            case "alliance":
                if (!tanPlayer.hasTown()) {
                    player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_NO_TOWN.get());
                    return;
                }

                TownData playerTown = tanPlayer.getTown();

                playerTown.getRelations().getTerritoriesIDWithRelation(TownRelation.ALLIANCE)
                        .forEach(territoryID -> Objects.requireNonNull(TerritoryUtil.getTerritory(territoryID))
                                .broadCastMessage(Lang.CHAT_SCOPE_ALLIANCE_MESSAGE.get(playerTown.getName(), player.getName(), message))
                        );
                return;
            case "region":
                if (!tanPlayer.hasRegion()) {
                    player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_NO_TOWN.get());
                    return;
                }

                RegionData regionData = tanPlayer.getRegion();
                if (regionData != null)
                    regionData.broadCastMessage(Lang.CHAT_SCOPE_REGION_MESSAGE.get(regionData.getName(), player.getName(), message));
                return;
            case "town":
                if (!tanPlayer.hasTown()) {
                    player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_NO_TOWN.get());
                    return;
                }

                TownData townData = tanPlayer.getTown();
                if (townData != null)
                    townData.broadCastMessage(Lang.CHAT_SCOPE_TOWN_MESSAGE.get(townData.getName(), player.getName(), message));
                return;
            default:
                player.sendMessage(TanChatUtils.getTANString() + Lang.CORRECT_SYNTAX_INFO.get(getSyntax()));
                break;
        }
    }

}


