package org.tan.TownsAndNations.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.tan.TownsAndNations.commands.debugsubcommands.*;
import org.tan.TownsAndNations.storage.PlayerDataStorage;

import java.util.ArrayList;
import java.util.List;

public class DebugCommandManager implements CommandExecutor, TabExecutor {

    private final ArrayList<SubCommand> subCommands = new ArrayList<>();

    public DebugCommandManager(){

        subCommands.add(new AddMoney());
        subCommands.add(new SetMoney());
        subCommands.add(new SpawnVillager());
        subCommands.add(new getRareItem());

        subCommands.add(new AddNewFeatures());
        subCommands.add(new ChatStorage());
        subCommands.add(new SaveAll());

        subCommands.add(new TownStat());

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

        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender,@NotNull Command command,@NotNull String label, String[] args) {
        List<String> suggestions = new ArrayList<>();

        // If the player is just starting to type the command
        if(args.length == 1) {
            for(SubCommand subCmd : subCommands) {
                // Add all sub-commands that start with the entered text
                if(subCmd.getName().startsWith(args[0].toLowerCase())) {
                    suggestions.add(subCmd.getName());
                }
            }
        }

        return suggestions;
    }

    public List<SubCommand> getSubcommands(){
        return subCommands;
    }


}
