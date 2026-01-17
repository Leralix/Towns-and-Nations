package org.leralix.tan.commands.admin;

import org.bukkit.command.CommandSender;
import org.leralix.lib.commands.SubCommand;
import org.leralix.tan.utils.text.NameFilter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NameFilterAdminCommand extends SubCommand {

    @Override
    public String getName() {
        return "namefilter";
    }

    @Override
    public String getDescription() {
        return "Manage name filter words list";
    }

    @Override
    public int getArguments() {
        return 1;
    }

    @Override
    public String getSyntax() {
        return "/tanadmin namefilter <add|remove|list|reload> [word]";
    }

    @Override
    public List<String> getTabCompleteSuggestions(CommandSender commandSender, String lowerCase, String[] args) {
        if (args.length == 2) {
            return List.of("add", "remove", "list", "reload");
        }
        if (args.length == 3 && ("remove".equalsIgnoreCase(args[1]) || "list".equalsIgnoreCase(args[1]))) {
            return NameFilter.listBlockedWords();
        }
        return Collections.emptyList();
    }

    @Override
    public void perform(CommandSender commandSender, String[] args) {
        if (args.length < 2) {
            commandSender.sendMessage(getSyntax());
            return;
        }

        String action = args[1].toLowerCase();
        switch (action) {
            case "reload" -> {
                NameFilter.reload();
                commandSender.sendMessage("Name filter reloaded.");
            }
            case "list" -> {
                List<String> words = NameFilter.listBlockedWords();
                if (words.isEmpty()) {
                    commandSender.sendMessage("Name filter list is empty.");
                    return;
                }
                commandSender.sendMessage("Name filter blocked words (" + words.size() + "):");
                commandSender.sendMessage(String.join(", ", words));
            }
            case "add" -> {
                if (args.length < 3) {
                    commandSender.sendMessage(getSyntax());
                    return;
                }
                String word = joinFrom(args, 2);
                boolean ok = NameFilter.addBlockedWord(word);
                if (ok) {
                    commandSender.sendMessage("Added blocked word: " + word);
                } else {
                    commandSender.sendMessage("Failed to add blocked word: " + word);
                }
            }
            case "remove" -> {
                if (args.length < 3) {
                    commandSender.sendMessage(getSyntax());
                    return;
                }
                String word = joinFrom(args, 2);
                boolean ok = NameFilter.removeBlockedWord(word);
                if (ok) {
                    commandSender.sendMessage("Removed blocked word: " + word);
                } else {
                    commandSender.sendMessage("Blocked word not found (or failed to remove): " + word);
                }
            }
            default -> commandSender.sendMessage(getSyntax());
        }
    }

    private static String joinFrom(String[] args, int index) {
        if (args.length <= index) {
            return "";
        }
        List<String> parts = new ArrayList<>();
        for (int i = index; i < args.length; i++) {
            if (args[i] != null && !args[i].isBlank()) {
                parts.add(args[i]);
            }
        }
        return String.join(" ", parts).trim();
    }
}
