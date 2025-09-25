package org.leralix.tan.commands.server;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.leralix.lib.commands.SubCommand;
import org.leralix.tan.gui.user.MainMenu;
import org.leralix.tan.lang.Lang;

import java.util.Collections;
import java.util.List;

class OpenGuiServer extends SubCommand {


    @Override
    public String getName() {
        return "gui";
    }

    @Override
    public String getDescription() {
        return Lang.OPEN_GUI_SERVER_DESC.getDefault();
    }

    @Override
    public int getArguments() {
        return 2;
    }

    @Override
    public String getSyntax() {
        return "/tanserver gui <player_username>";
    }

    @Override
    public List<String> getTabCompleteSuggestions(CommandSender player, String currentMessage, String[] args) {
        if(args.length == 2){
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
        }
        return Collections.emptyList();
    }

    @Override
    public void perform(CommandSender commandSender, String[] args) {
        if(args.length < 2){
            commandSender.sendMessage(Lang.INVALID_ARGUMENTS.getDefault());
            return;
        }
        String playerName = args[1];
        Player player = commandSender.getServer().getPlayer(playerName);
        if(player == null){
            commandSender.sendMessage(Lang.PLAYER_NOT_FOUND.getDefault());
            return;
        }
        new MainMenu(player);
        commandSender.sendMessage(Lang.COMMAND_GENERIC_SUCCESS.getDefault());

    }
}
