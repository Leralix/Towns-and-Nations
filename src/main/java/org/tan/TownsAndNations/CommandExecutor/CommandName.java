package org.tan.TownsAndNations.CommandExecutor;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.tan.TownsAndNations.utils.ConfigUtil;

public class CommandName implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (command.getName().equalsIgnoreCase("tantest")) {

            String commandeConfig = ConfigUtil.getCustomConfig("config.yml").getString("tanCommand", "/tan");

            commandSender.getServer().dispatchCommand(commandSender, commandeConfig);
            return true;
        }
        return false;
    }
}