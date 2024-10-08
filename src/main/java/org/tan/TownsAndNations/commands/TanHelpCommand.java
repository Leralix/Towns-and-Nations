package org.tan.TownsAndNations.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TanHelpCommand extends SubCommand {
    CommandManager commandManager;
    public TanHelpCommand(CommandManager commandManager){
        this.commandManager = commandManager;
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "explanation on every command";
    }

    @Override
    public int getArguments() {
        return 1;
    }

    @Override
    public String getSyntax() {
        return "/tan help <page n°>";
    }

    @Override
    public List<String> getTabCompleteSuggestions(Player player, String currentMessage, String[] args) {
        List<String> suggestions = new ArrayList<>();
        if(args.length == 2){
            suggestions.add("<page n°>");
        }
        return suggestions;
    }

    @Override
    public void perform(Player player, String[] args) {
        if(args.length == 1){
            sendHelp(player, 0);
        }else{
            try{
                int page = Integer.parseInt(args[1]);
                sendHelp(player, page);
            }catch (NumberFormatException e){
                player.sendMessage("Please enter a valid number");
            }
        }
    }
    private void sendHelp(Player p, int page) {
        List<SubCommand> commandList = new ArrayList<>(commandManager.getSubCommands());

        if(page < 0)
            page = 0;
        if(page > commandList.size() / 8)
            page = commandList.size() / 8;

        p.sendMessage("╭──────────⟢⟐⟣──────────╮");
        commandList.subList(page * 8, Math.min(commandList.size(), (page + 1) * 8)).forEach(subCommand -> p.sendMessage(subCommand.getSyntax() + ChatColor.GRAY + " - " + subCommand.getDescription()));

        ComponentBuilder pageLine = new ComponentBuilder();
        pageLine.append("╰───────");

        pageLine.append("<<");
        pageLine.color(ChatColor.GOLD);
        pageLine.bold(true);
        pageLine.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + commandManager.getName() + " help " + (page - 1)));
        pageLine.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to go to the previous page").create()));

        pageLine.append(" page n°" + page + " ");
        pageLine.color(ChatColor.WHITE);
        pageLine.bold(false);
        pageLine.event((ClickEvent) null);
        pageLine.event((HoverEvent) null);

        pageLine.append(">>");
        pageLine.color(ChatColor.GOLD);
        pageLine.bold(true);

        pageLine.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + commandManager.getName() + " help " + (page + 1)));
        pageLine.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to go to the next page").create()));

        pageLine.append("───────╯");
        pageLine.color(ChatColor.WHITE);
        pageLine.bold(false);
        pageLine.event((ClickEvent) null);
        pageLine.event((HoverEvent) null);
        p.spigot().sendMessage(pageLine.create());

    }

}
