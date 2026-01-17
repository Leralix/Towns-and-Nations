package org.leralix.tan.commands.admin;

import org.bukkit.command.CommandSender;
import org.leralix.lib.commands.SubCommand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NameFilterAdminCommand extends SubCommand {

    private static final String NAME_FILTER_CLASS = "org.leralix.tan.utils.text.NameFilter";

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
            return listBlockedWordsOrEmpty();
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
                if (!ensureNameFilterAvailable(commandSender)) {
                    return;
                }
                reloadNameFilter();
                commandSender.sendMessage("Name filter reloaded.");
            }
            case "list" -> {
                if (!ensureNameFilterAvailable(commandSender)) {
                    return;
                }
                List<String> words = listBlockedWordsOrEmpty();
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
                if (!ensureNameFilterAvailable(commandSender)) {
                    return;
                }
                String word = joinFrom(args, 2);
                boolean ok = addBlockedWord(word);
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
                if (!ensureNameFilterAvailable(commandSender)) {
                    return;
                }
                String word = joinFrom(args, 2);
                boolean ok = removeBlockedWord(word);
                if (ok) {
                    commandSender.sendMessage("Removed blocked word: " + word);
                } else {
                    commandSender.sendMessage("Blocked word not found (or failed to remove): " + word);
                }
            }
            default -> commandSender.sendMessage(getSyntax());
        }
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
        invokeStaticVoid("reload");
    }

    private static List<String> listBlockedWordsOrEmpty() {
        Object res = invokeStatic("listBlockedWords");
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

    private static boolean addBlockedWord(String word) {
        Object res = invokeStatic("addBlockedWord", new Class<?>[]{String.class}, new Object[]{word});
        return res instanceof Boolean b && b;
    }

    private static boolean removeBlockedWord(String word) {
        Object res = invokeStatic("removeBlockedWord", new Class<?>[]{String.class}, new Object[]{word});
        return res instanceof Boolean b && b;
    }

    private static void invokeStaticVoid(String methodName) {
        invokeStatic(methodName, new Class<?>[0], new Object[0]);
    }

    private static Object invokeStatic(String methodName) {
        return invokeStatic(methodName, new Class<?>[0], new Object[0]);
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
