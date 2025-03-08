package org.leralix.tan.commands.debugsubcommand;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.leralix.lib.commands.SubCommand;
import org.leralix.tan.lang.Lang;

import java.util.Collections;
import java.util.List;

public class ColorCode extends SubCommand {

    @Override
    public String getName() {
        return "ColorCode";
    }

    @Override
    public String getDescription() {
        return Lang.DEBUG_GET_COLOR_CODE.get();
    }

    @Override
    public int getArguments() {
        return 0;
    }

    @Override
    public String getSyntax() {
        return "/tandebug colorcode";
    }
    public List<String> getTabCompleteSuggestions(CommandSender commandSender, String lowerCase, String[] args){
        return Collections.emptyList();
    }
    @Override
    public void perform(CommandSender commandSender, String[] args) {
        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('§', "Player:§3 test"));
        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('§',"Money:§6 50✦"));
        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('§',"Town:§9 my town"));
        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('§',"Region:§b my region"));
        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('§',"Kingdom:§5 my kingdom"));
        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('§',"Chunks:§2 47/50♦"));
        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('§',"Number of Player:§9 6♣"));
        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('§',"other:§e status"));

    }

}
