package org.tan.TownsAndNations.commands.debugSubcommands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.commands.SubCommand;

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
    public List<String> getTabCompleteSuggestions(Player player, String[] args){
        return null;
    }
    @Override
    public void perform(Player player, String[] args) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('§', "Player:§3 test"));
        player.sendMessage(ChatColor.translateAlternateColorCodes('§',"Money:§6 50✦"));
        player.sendMessage(ChatColor.translateAlternateColorCodes('§',"Town:§9 my town"));
        player.sendMessage(ChatColor.translateAlternateColorCodes('§',"Region:§b my region"));
        player.sendMessage(ChatColor.translateAlternateColorCodes('§',"Kingdom:§5 my kingdom"));
        player.sendMessage(ChatColor.translateAlternateColorCodes('§',"Chunks:§2 47/50♦"));
        player.sendMessage(ChatColor.translateAlternateColorCodes('§',"Number of Player:§9 6♣"));
        player.sendMessage(ChatColor.translateAlternateColorCodes('§',"other:§e status"));

    }

}
