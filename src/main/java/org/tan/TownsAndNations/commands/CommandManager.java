package org.tan.TownsAndNations.commands;

import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.tan.TownsAndNations.commands.subcommands.*;
import org.tan.TownsAndNations.storage.PlayerDataStorage;

import java.util.ArrayList;
import java.util.List;

public class CommandManager implements CommandExecutor, TabExecutor, TabCompleter {

    private final ArrayList<SubCommand> subCommands = new ArrayList<>();

    public CommandManager(){

        subCommands.add(new InvitePlayerCommand());
        subCommands.add(new JoinTownCommand());

        subCommands.add(new ClaimCommand());
        subCommands.add(new UnclaimCommand());
        subCommands.add(new MapCommand());

        subCommands.add(new SeeBalanceCommand());
        subCommands.add(new PayCommand());
        subCommands.add(new OpenGuiCommand());
        subCommands.add(new AcceptRelationCommand());
        subCommands.add(new ChannelChatScopeCommand());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player p){

            if (args.length > 0){
                for (int i = 0; i < getSubCommands().size(); i++){
                    if (args[0].equalsIgnoreCase(getSubCommands().get(i).getName())){
                        getSubCommands().get(i).perform(p, args);

                        PlayerDataStorage.saveStats();
                        return true;
                    }
                }
                p.sendMessage("--------------------------------");
                for (int i = 0; i < getSubCommands().size(); i++){
                    p.sendMessage(getSubCommands().get(i).getSyntax() + " - " + getSubCommands().get(i).getDescription());
                }
                p.sendMessage("--------------------------------");
            }

        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> suggestions = new ArrayList<>();

        if(args.length == 1) {
            for(SubCommand subCmd : subCommands) {
                if(subCmd.getName().startsWith(args[0].toLowerCase())) {
                    suggestions.add(subCmd.getName());
                }
            }
        }else {
            SubCommand subCmd = subCommands.stream().filter(cmd -> cmd.getName().equalsIgnoreCase(args[0])).findFirst().orElse(null);
            if(subCmd != null && sender instanceof Player) {
                suggestions = subCmd.getTabCompleteSuggestions((Player) sender, args);
            }
        }

        return suggestions;
    }

    public List<SubCommand> getSubCommands(){
        return subCommands;
    }


}
