package org.leralix.tan.commands.admin;

import org.bukkit.command.CommandSender;
import org.leralix.lib.commands.SubCommand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class NameFilterAdminCommand extends SubCommand {

    private static final String NAME_FILTER_CLASS = "org.leralix.tan.utils.text.NameFilter";
    private static final Class<?>[] NO_TYPES = new Class<?>[0];
    private static final Object[] NO_ARGS = new Object[0];

    private static final String ACTION_ADD = "add";
    private static final String ACTION_REMOVE = "remove";
    private static final String ACTION_LIST = "list";
    private static final String ACTION_RELOAD = "reload";

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
            return List.of(ACTION_ADD, ACTION_REMOVE, ACTION_LIST, ACTION_RELOAD);
        }
        if (args.length == 3 && (ACTION_REMOVE.equalsIgnoreCase(args[1]) || ACTION_LIST.equalsIgnoreCase(args[1]))) {
            return listBlockedWordsOrEmpty();
        }
        return Collections.emptyList();
    }

    @Override
    public void perform(CommandSender commandSender, String[] args) {
        if (!requireMinArgs(commandSender, args, 2)) {
            return;
        }

        String action = args[1].toLowerCase(Locale.ROOT);

        if (isNameFilterAction(action) && !ensureNameFilterAvailable(commandSender)) {
            return;
        }

        switch (action) {
            case ACTION_RELOAD -> handleReload(commandSender);
            case ACTION_LIST -> handleList(commandSender);
            case ACTION_ADD -> handleAdd(commandSender, args);
            case ACTION_REMOVE -> handleRemove(commandSender, args);
            default -> commandSender.sendMessage(getSyntax());
        }
    }

    private static boolean isNameFilterAction(String action) {
        return ACTION_RELOAD.equals(action)
                || ACTION_LIST.equals(action)
                || ACTION_ADD.equals(action)
                || ACTION_REMOVE.equals(action);
    }

    private void handleReload(CommandSender sender) {
        reloadNameFilter();
        sender.sendMessage("Name filter reloaded.");
    }

    private void handleList(CommandSender sender) {
        List<String> words = listBlockedWordsOrEmpty();
        if (words.isEmpty()) {
            sender.sendMessage("Name filter list is empty.");
            return;
        }
        sender.sendMessage("Name filter blocked words (" + words.size() + "):");
        sender.sendMessage(String.join(", ", words));
    }

    private void handleAdd(CommandSender sender, String[] args) {
        handleWordMutation(sender, args, "addBlockedWord", "Added blocked word: ", "Failed to add blocked word: ");
    }

    private void handleRemove(CommandSender sender, String[] args) {
        handleWordMutation(sender, args, "removeBlockedWord", "Removed blocked word: ", "Blocked word not found (or failed to remove): ");
    }

    private void handleWordMutation(CommandSender sender, String[] args, String methodName, String okPrefix, String failPrefix) {
        if (!requireMinArgs(sender, args, 3)) {
            return;
        }
        String word = joinFrom(args, 2);
        boolean ok = invokeBooleanWithStringArg(methodName, word);
        sender.sendMessage((ok ? okPrefix : failPrefix) + word);
    }

    private boolean requireMinArgs(CommandSender sender, String[] args, int minArgs) {
        if (args.length < minArgs) {
            sender.sendMessage(getSyntax());
            return false;
        }
        return true;
    }

    private static boolean ensureNameFilterAvailable(CommandSender sender) {
        if (getNameFilterClass() != null) {
            return true;
        }
        sender.sendMessage("NameFilter is not available in this build.");
        return false;
    }

    private static Class<?> getNameFilterClass() {
        try {
            return Class.forName(NAME_FILTER_CLASS);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    private static void reloadNameFilter() {
        invokeStatic(ACTION_RELOAD, NO_TYPES, NO_ARGS);
    }

    private static List<String> listBlockedWordsOrEmpty() {
        Object res = invokeStatic("listBlockedWords", NO_TYPES, NO_ARGS);
        if (res instanceof List<?> list) {
            List<String> out = new ArrayList<>();
            for (Object o : list) {
                if (o != null) {
                    out.add(o.toString());
                }
            }
            return out;
        }
        return Collections.emptyList();
    }

    private static boolean invokeBooleanWithStringArg(String methodName, String value) {
        Object res = invokeStatic(methodName, new Class<?>[]{String.class}, new Object[]{value});
        return res instanceof Boolean b && b;
    }

    private static Object invokeStatic(String methodName, Class<?>[] parameterTypes, Object[] args) {
        Class<?> cls = getNameFilterClass();
        if (cls == null) {
            return null;
        }
        try {
            return cls.getMethod(methodName, parameterTypes).invoke(null, args);
        } catch (ReflectiveOperationException e) {
            return null;
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
