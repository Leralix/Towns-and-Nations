package org.leralix.tan.commands.adminsubcommand;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.leralix.lib.commands.SubCommand;
import org.leralix.tan.storage.SudoPlayerStorage;
import org.leralix.tan.utils.TanChatUtils;
import org.leralix.tan.lang.Lang;

import java.util.ArrayList;
import java.util.List;

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
            SudoPlayerStorage.swap(player);
        }
        else if (args.length == 2) {
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                player.sendMessage(TanChatUtils.getTANString() + Lang.PLAYER_NOT_FOUND.get());
                return;
            }
            if(target.getUniqueId().equals(player.getUniqueId())){
                SudoPlayerStorage.swap(player);
            }
            else {
                SudoPlayerStorage.swap(player, target);
            }
        }
        else {
            player.sendMessage(TanChatUtils.getTANString() + Lang.NOT_ENOUGH_ARGS_ERROR.get());
            player.sendMessage(TanChatUtils.getTANString() + Lang.CORRECT_SYNTAX_INFO.get(getSyntax()));
        }
    }
}
