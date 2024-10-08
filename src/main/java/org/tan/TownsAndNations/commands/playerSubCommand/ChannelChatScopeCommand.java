package org.tan.TownsAndNations.commands.playerSubCommand;

import org.bukkit.entity.Player;
import org.tan.TownsAndNations.DataClass.territoryData.TownData;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.commands.SubCommand;
import org.tan.TownsAndNations.enums.ChatScope;
import org.tan.TownsAndNations.storage.LocalChatStorage;
import org.tan.TownsAndNations.storage.DataStorage.TownDataStorage;

import java.util.ArrayList;
import java.util.List;

import static org.tan.TownsAndNations.utils.ChatUtils.getTANString;
public class ChannelChatScopeCommand extends SubCommand{
    @Override
    public String getName() {
        return "chat";
    }

    @Override
    public String getDescription() {
        return Lang.TOWN_CHAT_COMMAND_DESC.get();
    }
    public int getArguments(){ return 1;}


    @Override
    public String getSyntax() {
        return "/tan chat <channel name>";
    }
    @Override
    public List<String> getTabCompleteSuggestions(Player player, String lowerCase, String[] args){
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
    public void perform(Player player, String[] args){

        if (args.length == 1) {
            player.sendMessage(getTANString() + Lang.NOT_ENOUGH_ARGS_ERROR.get());
            player.sendMessage(getTANString() + Lang.CORRECT_SYNTAX_INFO.get(getSyntax()));
        } else if (args.length == 2){

            String channelName = args[1];
            TownData town = TownDataStorage.get(player);
            if(town == null){
                player.sendMessage(getTANString() + Lang.PLAYER_NO_TOWN.get());
                return;
            }
            if(channelName.equalsIgnoreCase("global")){
                LocalChatStorage.removePlayerChatScope(player);
                return;
            }
            if(channelName.equalsIgnoreCase("town")){

                if(LocalChatStorage.getPlayerChatScope(player) == ChatScope.CITY){
                    player.sendMessage(getTANString() + Lang.TOWN_CHAT_ALREADY_IN_CHAT.get(ChatScope.CITY));
                    return;
                }

                LocalChatStorage.setPlayerChatScope(player, ChatScope.CITY);
                player.sendMessage(getTANString() + Lang.CHAT_CHANGED.get(channelName));
                return;
            }
            if(channelName.equalsIgnoreCase("alliance")){

                if(LocalChatStorage.getPlayerChatScope(player) == ChatScope.ALLIANCE){
                    player.sendMessage(getTANString() + Lang.TOWN_CHAT_ALREADY_IN_CHAT.get(ChatScope.ALLIANCE));
                    return;
                }

                LocalChatStorage.setPlayerChatScope(player, ChatScope.ALLIANCE);
                player.sendMessage(getTANString() + Lang.CHAT_CHANGED.get(channelName));
                return;
            }
            if(channelName.equalsIgnoreCase("region")){

                if(LocalChatStorage.getPlayerChatScope(player) == ChatScope.REGION){
                    player.sendMessage(getTANString() + Lang.TOWN_CHAT_ALREADY_IN_CHAT.get(ChatScope.REGION));
                    return;
                }

                LocalChatStorage.setPlayerChatScope(player, ChatScope.REGION);
                player.sendMessage(getTANString() + Lang.CHAT_CHANGED.get(channelName));
                return;
            }

            player.sendMessage(getTANString() + Lang.CHAT_SCOPE_NOT_FOUND.get(channelName));

        }
        else{
            player.sendMessage(getTANString() + Lang.TOO_MANY_ARGS_ERROR.get());
            player.sendMessage(getTANString() + Lang.CORRECT_SYNTAX_INFO.get(getSyntax()));
        }
    }

}


