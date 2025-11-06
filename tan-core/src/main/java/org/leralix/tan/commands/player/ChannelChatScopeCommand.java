package org.leralix.tan.commands.player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.leralix.lib.commands.PlayerSubCommand;
import org.leralix.lib.data.SoundEnum;
import org.leralix.tan.dataclass.ITanPlayer;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.enums.ChatScope;
import org.leralix.tan.enums.TownRelation;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.lang.LangType;
import org.leralix.tan.storage.LocalChatStorage;
import org.leralix.tan.storage.stored.PlayerDataStorage;
import org.leralix.tan.utils.gameplay.TerritoryUtil;
import org.leralix.tan.utils.text.TanChatUtils;

public class ChannelChatScopeCommand extends PlayerSubCommand {

  private static final String TOWN = "town";
  private static final String ALLIANCE = "alliance";
  private static final String REGION = "region";
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
    return "/tan chat <global|alliance|region|town> [message]";
  }

  @Override
  public List<String> getTabCompleteSuggestions(Player player, String lowerCase, String[] args) {
    List<String> suggestions = new ArrayList<>();
    if (args.length == 2) {
      suggestions.add(TOWN);
      suggestions.add(ALLIANCE);
      suggestions.add(REGION);
      suggestions.add(GLOBAL);
    }
    return suggestions;
  }

  @Override
  public void perform(Player player, String[] args) {

    ITanPlayer tanPlayer = PlayerDataStorage.getInstance().getSync(player);
    LangType langType = tanPlayer.getLang();
    if (args.length == 1) {
      TanChatUtils.message(player, Lang.NOT_ENOUGH_ARGS_ERROR.get(langType), SoundEnum.NOT_ALLOWED);
      TanChatUtils.message(player, Lang.CORRECT_SYNTAX_INFO.get(langType, getSyntax()));
    } else if (args.length == 2) {
      registerPlayerToScope(player, args[1]);
    } else if (args.length >= 3) {
      sendSingleMessage(player, args[1], args);
    } else {
      TanChatUtils.message(player, Lang.TOO_MANY_ARGS_ERROR.get(langType), SoundEnum.NOT_ALLOWED);
      TanChatUtils.message(player, Lang.CORRECT_SYNTAX_INFO.get(langType, getSyntax()));
    }
  }

  private static void registerPlayerToScope(Player player, String channelName) {
    ITanPlayer tanPlayer = PlayerDataStorage.getInstance().getSync(player);
    LangType langType = tanPlayer.getLang();
    TownData town = tanPlayer.getTownSync();
    if (town == null) {
      TanChatUtils.message(player, Lang.PLAYER_NO_TOWN.get(langType));
      return;
    }

    if (channelName.equalsIgnoreCase(GLOBAL)) {
      LocalChatStorage.removePlayerChatScope(player);
      TanChatUtils.message(player, Lang.CHAT_CHANGED.get(langType, channelName));
      return;
    }
    ChatScope scope = LocalChatStorage.getPlayerChatScope(player);

    if (channelName.equalsIgnoreCase(TOWN)) {

      if (scope == ChatScope.CITY) {
        TanChatUtils.message(
            player, Lang.TOWN_CHAT_ALREADY_IN_CHAT.get(langType, ChatScope.CITY.getName(langType)));
        return;
      }

      LocalChatStorage.setPlayerChatScope(player, ChatScope.CITY);
      TanChatUtils.message(player, Lang.CHAT_CHANGED.get(langType, channelName));
      return;
    }
    if (channelName.equalsIgnoreCase(ALLIANCE)) {

      if (scope == ChatScope.ALLIANCE) {
        TanChatUtils.message(
            player,
            Lang.TOWN_CHAT_ALREADY_IN_CHAT.get(langType, ChatScope.ALLIANCE.getName(langType)));
        return;
      }

      LocalChatStorage.setPlayerChatScope(player, ChatScope.ALLIANCE);
      TanChatUtils.message(player, Lang.CHAT_CHANGED.get(langType, channelName));
      return;
    }
    if (channelName.equalsIgnoreCase(REGION)) {

      if (scope == ChatScope.REGION) {
        TanChatUtils.message(
            player,
            Lang.TOWN_CHAT_ALREADY_IN_CHAT.get(langType, ChatScope.REGION.getName(langType)));
        return;
      }

      LocalChatStorage.setPlayerChatScope(player, ChatScope.REGION);
      TanChatUtils.message(player, Lang.CHAT_CHANGED.get(langType, channelName));
      return;
    }
    TanChatUtils.message(player, Lang.CHAT_SCOPE_NOT_FOUND.get(langType, channelName));
  }

  private void sendSingleMessage(Player player, String channelName, String[] words) {
    ITanPlayer tanPlayer = PlayerDataStorage.getInstance().getSync(player);
    LangType langType = tanPlayer.getLang();
    String message = String.join(" ", Arrays.copyOfRange(words, 2, words.length));

    switch (channelName.toLowerCase()) {
      case "global":
        // Send message to global chat by broadcasting it with Adventure API
        if (LocalChatStorage.isPlayerInChatScope(player.getUniqueId().toString())) {
          ChatScope prevScope = LocalChatStorage.getPlayerChatScope(player);
          LocalChatStorage.removePlayerChatScope(player);
          Component formattedMessage =
              Component.text(String.format("<%s> %s", player.getName(), message));
          Bukkit.broadcast(formattedMessage);
          LocalChatStorage.setPlayerChatScope(player, prevScope);
        } else {
          Component formattedMessage =
              Component.text(String.format("<%s> %s", player.getName(), message));
          Bukkit.broadcast(formattedMessage);
        }
        return;
      case "alliance":
        if (!tanPlayer.hasTown()) {
          TanChatUtils.message(player, Lang.PLAYER_NO_TOWN.get(langType));
          return;
        }

        TownData playerTown = tanPlayer.getTownSync();
        playerTown
            .getRelations()
            .getTerritoriesIDWithRelation(TownRelation.ALLIANCE)
            .forEach(
                territoryID ->
                    Objects.requireNonNull(TerritoryUtil.getTerritory(territoryID))
                        .broadCastMessage(
                            Lang.CHAT_SCOPE_ALLIANCE_MESSAGE.get(
                                playerTown.getName(), player.getName(), message)));
        return;
      case "region":
        if (!tanPlayer.hasRegion()) {
          TanChatUtils.message(player, Lang.PLAYER_NO_TOWN.get(langType));
          return;
        }

        RegionData regionData = tanPlayer.getRegionSync();
        if (regionData != null)
          regionData.broadCastMessage(
              Lang.CHAT_SCOPE_REGION_MESSAGE.get(regionData.getName(), player.getName(), message));
        return;
      case "town":
        if (!tanPlayer.hasTown()) {
          TanChatUtils.message(player, Lang.PLAYER_NO_TOWN.get(langType));
          return;
        }

        TownData townData = tanPlayer.getTownSync();
        if (townData != null)
          townData.broadCastMessage(
              Lang.CHAT_SCOPE_TOWN_MESSAGE.get(townData.getName(), player.getName(), message));
        return;
      default:
        TanChatUtils.message(player, Lang.CORRECT_SYNTAX_INFO.get(langType, getSyntax()));
        break;
    }
  }
}
