package org.leralix.tan.commands.admin;

import org.bukkit.command.CommandSender;
import org.leralix.lib.commands.SubCommand;
import org.leralix.tan.lang.Lang;
import org.leralix.tan.utils.text.NameFilter;
import org.leralix.tan.utils.text.TanChatUtils;

import java.util.*;

public class NameFilterAdminCommand extends SubCommand {

    private static final String ACTION_ADD = "add";
    private static final String ACTION_REMOVE = "remove";
    private static final String ACTION_LIST = "list";

    @Override
    public String getName() { return "namefilter"; }

    @Override
    public String getDescription() { return Lang.ADMIN_NAME_FILTER_DESC.getDefault(); }

    @Override
    public int getArguments() { return 1; }

    @Override
    public String getSyntax() { return "/tanadmin namefilter <add|remove|list|reload> [word]"; }

    @Override
    public List<String> getTabCompleteSuggestions(CommandSender commandSender, String lowerCase, String[] args) {
        if (args.length == 2) {
            return List.of(ACTION_ADD, ACTION_REMOVE, ACTION_LIST);
        }
        if (args.length == 3 && (ACTION_REMOVE.equalsIgnoreCase(args[1]))) {
            return NameFilter.getBlockedWords().stream().toList();
        }
        return Collections.emptyList();
    }

    @Override
    public void perform(CommandSender commandSender, String[] args) {
        if (!requireMinArgs(commandSender, args, 2)) {
            return;
        }

        String action = args[1].toLowerCase(Locale.ROOT);

        switch (action) {
            case ACTION_LIST -> handleList(commandSender);
            case ACTION_ADD -> handleAdd(commandSender, args);
            case ACTION_REMOVE -> handleRemove(commandSender, args);
            default -> commandSender.sendMessage(getSyntax());
        }
    }

    private void handleList(CommandSender sender) {
        Set<String> words = NameFilter.getBlockedWords();
        if (words.isEmpty()) {
            TanChatUtils.message(sender, Lang.ADMIN_NAME_FILTER_LIST_EMPTY);
            return;
        }
        TanChatUtils.message(sender, Lang.ADMIN_NAME_FILTER_LIST_HEADER.get(Integer.toString(words.size())));
        sender.sendMessage(String.join(", ", words));
    }

    private void handleAdd(CommandSender sender, String[] args) {
        String word = joinFrom(args);
        if(NameFilter.getBlockedWords().contains(word)){
            TanChatUtils.message(sender, Lang.ADMIN_NAME_FILTER_ADD_FAILED.get(word));
            return;
        }
        TanChatUtils.message(sender, Lang.ADMIN_NAME_FILTER_ADD_SUCCESS.get(word));
        NameFilter.addWord(word);
    }

    private void handleRemove(CommandSender sender, String[] args) {
        String word = joinFrom(args);
        if(!NameFilter.getBlockedWords().contains(word)){
            TanChatUtils.message(sender, Lang.ADMIN_NAME_FILTER_REMOVE_FAILED.get(word));
            return;
        }
        TanChatUtils.message(sender, Lang.ADMIN_NAME_FILTER_ADD_SUCCESS.get(word));
        NameFilter.removeWord(word);
    }

    private boolean requireMinArgs(CommandSender sender, String[] args, int minArgs) {
        if (args.length < minArgs) {
            sender.sendMessage(getSyntax());
            return false;
        }
        return true;
    }

    private static String joinFrom(String[] args) {
        if (args.length <= 2) {
            return "";
        }
        List<String> parts = new ArrayList<>();
        for (int i = 2; i < args.length; i++) {
            if (args[i] != null && !args[i].isBlank()) {
                parts.add(args[i]);
            }
        }
        return String.join(" ", parts).trim();
    }
}
