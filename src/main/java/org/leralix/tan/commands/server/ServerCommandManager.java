package org.leralix.tan.commands.server;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.leralix.tan.commands.CommandManager;
import org.leralix.tan.commands.SubCommand;
import org.leralix.tan.commands.TanHelpCommand;
import org.leralix.tan.commands.adminsubcommand.*;
import org.leralix.tan.listeners.chat.events.CreateTown;

public class ServerCommandManager extends CommandManager {

    public ServerCommandManager(){
        addSubCommand(new CreateTownServer());
        addSubCommand(new ApplyTownServer());
        addSubCommand(new QuitTownServer());
        addSubCommand(new DisbandTownServer());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length > 0){
            ServerSubCommand subCommand = (ServerSubCommand) subCommands.get(args[0]);
            if(subCommand != null) {
                subCommand.perform(sender, args);
                return true;
            }
        }

        return true;
    }


    @Override
    public String getName() {
        return "tanserver";
    }


}
