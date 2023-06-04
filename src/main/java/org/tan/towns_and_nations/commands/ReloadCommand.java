package org.tan.towns_and_nations.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.tan.towns_and_nations.TownsAndNations;

public class ReloadCommand extends CommandManager{

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("tanreload")) {
            if (sender.hasPermission("yourplugin.reload")) {
                TownsAndNations.getPlugin().reloadConfig(); // recharger la configuration
                sender.sendMessage(ChatColor.GREEN + "Le plugin a été rechargé !");
                return true;
            } else {
                sender.sendMessage(ChatColor.RED + "Vous n'avez pas la permission de recharger ce plugin !");
                return false;
            }
        }
        return false;
    }


}
