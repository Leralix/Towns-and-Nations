package org.tan.TownsAndNations.commands.adminSubcommands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.commands.SubCommand;
import org.tan.TownsAndNations.storage.SudoPlayerStorage;
import org.tan.TownsAndNations.utils.FileUtil;

import java.util.ArrayList;
import java.util.List;

import static org.tan.TownsAndNations.utils.ChatUtils.getTANString;

public class SudoPlayer extends SubCommand {

    @Override
    public String getName() {
        return "sudo";
    }

    @Override
    public String getDescription() {
        return Lang.ADMIN_SUDO_COMMAND.get();
    }

    @Override
    public int getArguments() {
        return 2;
    }

    @Override
    public String getSyntax() {
        return "/tanadmin sudo <optional - player> ";
    }
    public List<String> getTabCompleteSuggestions(Player player, String lowerCase, String[] args){

        List<String> suggestions = new ArrayList<>();
        if (args.length == 2) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                suggestions.add(p.getName());
            }
        }
        return suggestions;
    }
    @Override
    public void perform(Player player, String[] args) {

        if (args.length == 1) {
            if(SudoPlayerStorage.isSudoPlayer(player)){
                SudoPlayerStorage.removeSudoPlayer(player);
                player.sendMessage(getTANString() + Lang.SUDO_PLAYER_REMOVED.get(player.getName()));
                FileUtil.addLineToHistory(Lang.HISTORY_SUDO_MODE_REMOVED.get(player.getName(),player.getName()));
            }
            else{
                SudoPlayerStorage.addSudoPlayer(player);
                player.sendMessage(getTANString() + Lang.SUDO_PLAYER_ADDED.get(player.getName()));
                FileUtil.addLineToHistory(Lang.HISTORY_SUDO_MODE.get(player.getName(),player.getName()));

            }

        }
        else if (args.length == 2) {
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                player.sendMessage(getTANString() + Lang.PLAYER_NOT_FOUND.get());
                return;
            }
            if(SudoPlayerStorage.isSudoPlayer(player)){
                SudoPlayerStorage.removeSudoPlayer(player);
                player.sendMessage(getTANString() + Lang.SUDO_PLAYER_REMOVED.get(target.getName()));
                FileUtil.addLineToHistory(Lang.HISTORY_SUDO_MODE_REMOVED.get(player.getName(),target.getName()));
                if(!player.equals(target)) {
                    target.sendMessage(getTANString() + Lang.SUDO_PLAYER_REMOVED.get(target.getName()));
                    FileUtil.addLineToHistory(Lang.HISTORY_SUDO_MODE_REMOVED.get(target.getName(),target.getName()));
                }
            }
            else{
                SudoPlayerStorage.addSudoPlayer(player);
                player.sendMessage(getTANString() + Lang.SUDO_PLAYER_ADDED.get(target.getName()));
                FileUtil.addLineToHistory(Lang.HISTORY_SUDO_MODE.get(player.getName(),target.getName()));
                if(!player.equals(target)) {
                    target.sendMessage(getTANString() + Lang.SUDO_PLAYER_ADDED.get(target.getName()));
                    FileUtil.addLineToHistory(Lang.HISTORY_SUDO_MODE.get(target.getName(),target.getName()));
                }
            }
        }
        else {
            player.sendMessage(getTANString() + Lang.NOT_ENOUGH_ARGS_ERROR.get());
            player.sendMessage(getTANString() + Lang.CORRECT_SYNTAX_INFO.get(getSyntax()));
        }
    }
}
