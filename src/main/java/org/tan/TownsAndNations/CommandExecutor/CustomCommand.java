package org.tan.TownsAndNations.CommandExecutor;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.tan.TownsAndNations.utils.config.ConfigTag;
import org.tan.TownsAndNations.utils.config.ConfigUtil;

public class CustomCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {


        if (command.getName().equalsIgnoreCase(ConfigUtil.getCustomConfig(ConfigTag.MAIN).getString("tanCommand", "/tan"))) {

            commandSender.getServer().dispatchCommand(commandSender, "/tan");
            return true;
        }
        return false;



    }
}