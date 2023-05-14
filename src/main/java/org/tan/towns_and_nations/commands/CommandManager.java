package org.tan.towns_and_nations.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.tan.towns_and_nations.commands.subcommands.ChunkClaimCommand;
import org.tan.towns_and_nations.commands.subcommands.TownCreateCommand;

import java.util.ArrayList;

public class CommandManager implements CommandExecutor {

    private ArrayList<SubCommand> subcommands = new ArrayList<>();

    public CommandManager(){
        subcommands.add(new TownCreateCommand());
        subcommands.add(new ChunkClaimCommand());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player){
            Player p = (Player) sender;

            if (args.length > 0){
                for (int i = 0; i < getSubcommands().size(); i++){
                    if (args[0].equalsIgnoreCase(getSubcommands().get(i).getName())){
                        getSubcommands().get(i).perform(p, args);
                    }
                }
            }else {
                p.sendMessage("--------------------------------");
                for (int i = 0; i < getSubcommands().size(); i++){
                    p.sendMessage(getSubcommands().get(i).getSyntax() + " - " + getSubcommands().get(i).getDescription());
                }
                p.sendMessage("--------------------------------");
            }

        }


        return true;
    }

    public ArrayList<SubCommand> getSubcommands(){
        return subcommands;
    }

}
