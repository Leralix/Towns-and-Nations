package org.tan.towns_and_nations.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.tan.towns_and_nations.commands.subcommands.*;

import java.util.ArrayList;
import java.util.List;

public class CommandManager implements CommandExecutor, TabExecutor {

    private ArrayList<SubCommand> subcommands = new ArrayList<>();

    public CommandManager(){
        subcommands.add(new TownCreateCommand());
        subcommands.add(new ChunkClaimCommand());

        subcommands.add(new SeeBalanceCommand());
        subcommands.add(new AddBalanceCommand());
        subcommands.add(new PayCommand());
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

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {


        if (args.length == 1){
            List<String> TabCompleteList = new ArrayList<>();
            for (SubCommand subCommand : subcommands) {
                TabCompleteList.add(subCommand.getName());
            }


            return TabCompleteList;

            /*List<String> playerNames = new ArrayList<>();
            Player[] players = new Player[Bukkit.getServer().getOnlinePlayers().size()];
            Bukkit.getServer().getOnlinePlayers().toArray(players);
            for (int i = 0; i < players.length; i++){
                playerNames.add(players[i].getName());
            }



            return playerNames;
            */

        }
        /*else if (args.length == 2){
            List<String> arguments = new ArrayList<>();
            arguments.add("Daddy");
            arguments.add("JamesHarden#1");

            return arguments;
        }
        */
        return null;

    }

    public ArrayList<SubCommand> getSubcommands(){
        return subcommands;
    }

}
