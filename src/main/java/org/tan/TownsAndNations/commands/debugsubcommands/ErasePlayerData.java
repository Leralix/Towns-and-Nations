package org.tan.TownsAndNations.commands.debugsubcommands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.commands.SubCommand;
import org.tan.TownsAndNations.storage.PlayerDataStorage;
import org.tan.TownsAndNations.utils.ChatUtils;

import java.util.ArrayList;
import java.util.List;

import static org.tan.TownsAndNations.utils.ChatUtils.getTANString;

public class ErasePlayerData extends SubCommand {

    @Override
    public String getName() {
        return "erasePlayerData";
    }

    @Override
    public String getDescription() {
        return "erase player data";
    }

    @Override
    public int getArguments() {
        return 2;
    }

    @Override
    public String getSyntax() {
        return "/tandebug eraseplayerdata <player>";
    }
    public List<String> getTabCompleteSuggestions(Player player, String[] args){

        List<String> suggestions = new ArrayList<>();
        if (args.length == 2) {
            for (Player p : player.getServer().getOnlinePlayers()) {
                suggestions.add(p.getName());
            }
        }
        return suggestions;
    }
    @Override
    public void perform(Player player, String[] args) {


        if (args.length == 1) {
            player.sendMessage(getTANString() + Lang.NOT_ENOUGH_ARGS_ERROR.get());
            player.sendMessage(getTANString() + Lang.CORRECT_SYNTAX_INFO.get(getSyntax()));
        }
        else if (args.length == 2) {
            String targetID = Bukkit.getServer().getOfflinePlayer(args[1]).getUniqueId().toString();
            PlayerDataStorage.deleteData(targetID);

        }
        else {
            player.sendMessage(getTANString() + Lang.TOO_MANY_ARGS_ERROR.get());
            player.sendMessage(getTANString() + Lang.CORRECT_SYNTAX_INFO.get(getSyntax()));
        }
        player.sendMessage(ChatUtils.getTANString() + Lang.COMMAND_GENERIC_SUCCESS.get());
    }
}
