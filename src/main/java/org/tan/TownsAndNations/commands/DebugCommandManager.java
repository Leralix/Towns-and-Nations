package org.tan.TownsAndNations.commands;

import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.tan.TownsAndNations.commands.debugsubcommands.*;
import org.tan.TownsAndNations.storage.DataStorage.PlayerDataStorage;

import java.util.ArrayList;
import java.util.List;

public class DebugCommandManager implements CommandExecutor, TabExecutor, TabCompleter {

    private final ArrayList<SubCommand> subCommands = new ArrayList<>();

    public DebugCommandManager(){

        subCommands.add(new ChatStorage());
        subCommands.add(new SaveData());
        subCommands.add(new CreateBackup());

        subCommands.add(new ColorCode());

        subCommands.add(new SkipDay());
        subCommands.add(new PlaySound());
        subCommands.add(new ActionBarCommand());
        subCommands.add(new GetDropChances());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        if (sender instanceof Player p){

            if (args.length > 0){
                for (int i = 0; i < getSubcommands().size(); i++){
                    if (args[0].equalsIgnoreCase(getSubcommands().get(i).getName())){
                        getSubcommands().get(i).perform(p, args);

                        PlayerDataStorage.saveStats();
                        return true;
                    }
                }
                p.sendMessage("--------------------------------");
                for (int i = 0; i < getSubcommands().size(); i++){
                    p.sendMessage(getSubcommands().get(i).getSyntax() + " - " + getSubcommands().get(i).getDescription());
                }
                p.sendMessage("--------------------------------");
            }
            if(args.length == 0){
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
    public List<String> onTabComplete(@NotNull CommandSender sender,@NotNull Command command,@NotNull String label, String[] args) {
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

    public List<SubCommand> getSubcommands(){
        return subCommands;
    }


}
