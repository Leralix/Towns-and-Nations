package org.tan.TownsAndNations.commands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class SubCommand {

    public abstract String getName();

    public abstract String getDescription();
    public abstract int getArguments();

    public abstract String getSyntax();
    public abstract List<String> getTabCompleteSuggestions(Player player, String currentMessage, String[] args);

    public abstract void perform(Player player, String[] args);

    @NotNull
    public static List<String> payPlayerSuggestion(String[] args) {
        List<String> suggestions = new ArrayList<>();
        if (args.length == 2) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                suggestions.add(p.getName());
            }
        }
        if (args.length == 3) {
            suggestions.add("<amount>");
        }
        return suggestions;
    }

}
