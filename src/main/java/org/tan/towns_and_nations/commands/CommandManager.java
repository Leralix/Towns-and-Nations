package org.tan.towns_and_nations.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.tan.towns_and_nations.commands.subcommands.*;
import org.tan.towns_and_nations.utils.PlayerStatStorage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CommandManager implements CommandExecutor, TabExecutor {

    private ArrayList<SubCommand> subcommands = new ArrayList<>();

    public CommandManager(){

        subcommands.add(new InvitePlayerCommand());
        subcommands.add(new JoinTownCommand());


        subcommands.add(new SeeBalanceCommand());
        subcommands.add(new AddBalanceCommand());
        subcommands.add(new PayCommand());
        subcommands.add(new OpenGuiCommand());
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

        try {
            PlayerStatStorage.saveStats();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {




        if (args.length == 1){
            List<String> TabCompleteList = new ArrayList<>();
            for (SubCommand subCommand : subcommands) {
                TabCompleteList.add(subCommand.getName());
            }

            return TabCompleteList;

        }
        return null;

    }

    public ArrayList<SubCommand> getSubcommands(){
        return subcommands;
    }


}
