package org.leralix.tan.commands.server;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.leralix.lib.commands.CommandManager;
import org.leralix.lib.commands.SubCommand;

public class ServerCommandManager extends CommandManager {

    public ServerCommandManager(){
        super("tan.server");
        addSubCommand(new CreateTownServer());
        addSubCommand(new ApplyTownServer());
        addSubCommand(new QuitTownServer());
        addSubCommand(new DisbandTownServer());
        addSubCommand(new OpenGuiServer());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length > 0){
            SubCommand subCommand = subCommands.get(args[0]);
            if(subCommand != null) {
                subCommand.perform(sender, args);
                return true;
            }
        }
        return false;
    }


    @Override
    public String getName() {
        return "tanserver";
    }


}
