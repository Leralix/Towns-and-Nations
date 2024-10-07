package org.tan.TownsAndNations.commands.debugSubcommands;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.tan.TownsAndNations.commands.SubCommand;

import java.util.ArrayList;
import java.util.List;

public class ActionBarCommand extends SubCommand {

    @Override
    public String getName() {
        return "actionbar";
    }

    @Override
    public String getDescription() {
        return "show action bar";
    }

    @Override
    public int getArguments() {
        return 0;
    }

    @Override
    public String getSyntax() {
        return "/tandebug actionbar";
    }
    public List<String> getTabCompleteSuggestions(Player player, String lowerCase, String[] args){
        return new ArrayList<>();
    }
    @Override
    public void perform(Player player, String[] args) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(args[1]));


    }

}
