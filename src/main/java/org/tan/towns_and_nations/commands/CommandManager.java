package org.tan.towns_and_nations.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.tan.towns_and_nations.commands.subcommands.*;
import org.tan.towns_and_nations.storage.PlayerStatStorage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CommandManager implements CommandExecutor, TabExecutor {

    private final ArrayList<SubCommand> subcommands = new ArrayList<>();

    public CommandManager(){

        subcommands.add(new InvitePlayerCommand());
        subcommands.add(new JoinTownCommand());

        subcommands.add(new ClaimCommand());
        subcommands.add(new UnclaimCommand());

        subcommands.add(new SeeBalanceCommand());
        subcommands.add(new PayCommand());
        subcommands.add(new OpenGuiCommand());
        subcommands.add(new AcceptRelationCommand());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player p){

            if (args.length > 0){
                for (int i = 0; i < getSubcommands().size(); i++){
                    if (args[0].equalsIgnoreCase(getSubcommands().get(i).getName())){
                        getSubcommands().get(i).perform(p, args);

                        PlayerStatStorage.saveStats();
                        return true;
                    }
                }
                p.sendMessage("--------------------------------");
                for (int i = 0; i < getSubcommands().size(); i++){
                    p.sendMessage(getSubcommands().get(i).getSyntax() + " - " + getSubcommands().get(i).getDescription());
                }
                p.sendMessage("--------------------------------");
            }

        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        if (args.length == 1){
            List<String> TabCompleteList = new ArrayList<>();
            for (SubCommand subCommand : subcommands) {
                if (subCommand.getName().startsWith(args[0])) {
                    TabCompleteList.add(subCommand.getName());
                }
            }
            return TabCompleteList;
        }
        return null;

    }

    public ArrayList<SubCommand> getSubcommands(){
        return subcommands;
    }


}
